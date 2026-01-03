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
    public String generateRefreshToken(String idUser, String deviceId) {
        UUID userId = UUID.fromString(idUser);
        User user = userRepository.findById(userId).orElseThrow();

        String normalizedDeviceId = normalizeDeviceId(deviceId);

        if (normalizedDeviceId != null) {
            tokenRepository.deleteByUserAndDeviceId(user, normalizedDeviceId);
            tokenRepository.flush();
        }

        String tokenValue = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken(
                tokenValue,
                LocalDateTime.now().plusDays(30),
                user,
                normalizedDeviceId
        );

        tokenRepository.save(refreshToken);

        return tokenValue;
    }

    @Override
    @Transactional
    public TokenPair validateRefreshToken(String tokenValue,  String deviceId) {
        RefreshToken refreshToken = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            // On supprime le token expiré
            tokenRepository.delete(refreshToken);
            tokenRepository.flush();
            throw new RuntimeException("Refresh token expired");
        }

        String normalizedDeviceId = normalizeDeviceId(deviceId);

        // Si deviceId fourni, on vérifie qu'il correspond (anti-vol)
        if (normalizedDeviceId != null) {
            String storedDeviceId = refreshToken.getDeviceId();
            if (storedDeviceId == null || !storedDeviceId.equals(normalizedDeviceId)) {
                throw new RuntimeException("Refresh token does not match device");
            }
        }

        User user = refreshToken.getUser();

        // Rotation multi-devices : on supprime seulement ce refresh token
        tokenRepository.delete(refreshToken);
        tokenRepository.flush();

        // On crée un nouveau refresh token (nouvelle session pour ce device)
        String newRefreshToken = generateRefreshToken(user.getId().toString(), normalizedDeviceId);
        String newJwt = jwtUtil.generateToken(user);

        return new TokenPair(newRefreshToken, newJwt);
    }

    @Override
    @Transactional
    public void logout(String refreshToken, String deviceId) {
        String normalizedDeviceId = normalizeDeviceId(deviceId);

        tokenRepository.findByToken(refreshToken).ifPresent(rt -> {
            // Si deviceId fourni, on check avant de delete
            if (normalizedDeviceId != null) {
                if (normalizedDeviceId.equals(rt.getDeviceId())) {
                    tokenRepository.delete(rt);
                }
            } else {
                // Pas de deviceId (Postman / script / client legacy)
                tokenRepository.delete(rt);
            }
        });
    }

    @Transactional
    @Scheduled(fixedDelay = 24, timeUnit = TimeUnit.HOURS)
    void cleanExpiredTokens() {
        tokenRepository.deleteExpired();
    }

    private String normalizeDeviceId(String deviceId) {
        return (deviceId == null || deviceId.isBlank()) ? null : deviceId;
    }
}
