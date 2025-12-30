package com.humanbooster.cda.plugzy.security;

import com.humanbooster.cda.plugzy.controller.dto.auth.LoginCredentialsDTO;
import com.humanbooster.cda.plugzy.controller.dto.auth.LoginResponseDTO;
import com.humanbooster.cda.plugzy.controller.dto.user.UserPublicDTO;
import com.humanbooster.cda.plugzy.controller.dto.mapper.UserMapper;
import com.humanbooster.cda.plugzy.entity.RefreshToken;
import com.humanbooster.cda.plugzy.entity.User;
import com.humanbooster.cda.plugzy.repository.RefreshTokenRepository;
import com.humanbooster.cda.plugzy.repository.UserRepository;
import com.humanbooster.cda.plugzy.security.jwt.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final RefreshTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public AuthServiceImpl(AuthenticationManager authManager,
                           JwtUtil jwtUtil,
                           UserMapper userMapper,
                           RefreshTokenRepository tokenRepository,
                           UserRepository userRepository) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public LoginResponseDTO login(LoginCredentialsDTO credentials) {

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.getEmail(),
                        credentials.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();

        String jwt = jwtUtil.generateToken(user);
        UserPublicDTO userDTO = userMapper.convertToDTO(user);

        return new LoginResponseDTO(jwt, userDTO);
    }

    @Override
    @Transactional
    public String generateRefreshToken(String idUser) {
        UUID userId = UUID.fromString(idUser);
        User user = userRepository.findById(userId).orElseThrow();

        // Multi-devices : on NE supprime PAS les tokens existants
        String tokenValue = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken(
                tokenValue,
                LocalDateTime.now().plus(30, ChronoUnit.DAYS),
                user
        );

        tokenRepository.save(refreshToken);

        return tokenValue;
    }

    @Override
    @Transactional
    public TokenPair validateRefreshToken(String tokenValue) {
        RefreshToken refreshToken = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            // On supprime le token expiré
            tokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        User user = refreshToken.getUser();

        // Rotation multi-devices : on supprime seulement ce refresh token
        tokenRepository.delete(refreshToken);

        // On crée un nouveau refresh token (nouvelle session pour ce device)
        String newRefreshToken = generateRefreshToken(user.getId().toString());
        String newJwt = jwtUtil.generateToken(user);

        return new TokenPair(newRefreshToken, newJwt);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        tokenRepository.findByToken(refreshToken).ifPresent(tokenRepository::delete);
    }

    @Transactional
    @Scheduled(fixedDelay = 24, timeUnit = TimeUnit.HOURS)
    void cleanExpiredTokens() {
        tokenRepository.deleteExpired();
    }
}
