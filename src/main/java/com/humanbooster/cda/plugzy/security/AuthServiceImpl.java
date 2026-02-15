package com.humanbooster.cda.plugzy.security;

import com.humanbooster.cda.plugzy.controller.dto.auth.*;
import com.humanbooster.cda.plugzy.controller.dto.user.UserPublicDTO;
import com.humanbooster.cda.plugzy.controller.dto.mapper.UserMapper;
import com.humanbooster.cda.plugzy.entity.RefreshToken;
import com.humanbooster.cda.plugzy.entity.User;
import com.humanbooster.cda.plugzy.entity.Role;
import com.humanbooster.cda.plugzy.repository.RefreshTokenRepository;
import com.humanbooster.cda.plugzy.repository.RoleRepository;
import com.humanbooster.cda.plugzy.repository.UserRepository;
import com.humanbooster.cda.plugzy.security.jwt.JwtUtil;
import com.humanbooster.cda.plugzy.service.MailService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final RefreshTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;


    public AuthServiceImpl(AuthenticationManager authManager,
                           JwtUtil jwtUtil,
                           UserMapper userMapper,
                           RefreshTokenRepository tokenRepository,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           MailService mailService) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    /**
     * Authentifie un utilisateur et génère un JWT si les identifiants sont valides.
     *
     * @param credentials Les informations de connexion (email / mot de passe)
     * @return Un DTO contenant le JWT et les informations publiques de l'utilisateur
     * @throws ResponseStatusException si le compte n'est pas vérifié
     */
    @Override
    public LoginResponseDTO login(LoginCredentialsDTO credentials) {

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.getEmail(),
                        credentials.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();

        // On refuse le login si pas vérifié
        if (!user.isVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Compte non vérifié");
        }

        String jwt = jwtUtil.generateToken(user);
        UserPublicDTO userDTO = userMapper.convertToDTO(user);

        return new LoginResponseDTO(jwt, userDTO);
    }

    /**
     * Crée un nouvel utilisateur avec rôle par défaut ROLE_USER.
     * Génère un code de vérification temporaire envoyé par email.
     *
     * @param request Données d'inscription
     * @return Informations publiques du nouvel utilisateur
     * @throws ResponseStatusException si email ou username déjà utilisés
     */
    @Override
    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username déjà utilisé");
        }

        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ROLE_USER introuvable"));

        User u = new User();
        u.setUsername(request.getUsername());
        u.setEmail(request.getEmail());
        u.setPassword(passwordEncoder.encode(request.getPassword()));
        u.setPhone(request.getPhone());
        u.setRole(roleUser);

        u.setVerified(false);

        String code = generate6Digits();
        u.setVerificationCode(code);
        u.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));

        User saved = userRepository.save(u);

        // envoi mail
        mailService.sendVerificationCode(saved.getEmail(), code);

        return new RegisterResponseDTO(saved.getId(), saved.getEmail(), saved.isVerified());
    }

    /**
     * Valide le compte utilisateur à l'aide d'un code de vérification temporaire.
     *
     * @param request Contient l'email et le code de vérification
     * @throws ResponseStatusException si le code est invalide ou expiré
     */
    @Override
    @Transactional
    public void verify(VerifyRequestDTO request) {

        User u = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        if (u.isVerified()) return;

        if (u.getVerificationCode() == null || u.getVerificationCodeExpiresAt() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aucun code en attente");
        }

        if (u.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code expiré");
        }

        if (!u.getVerificationCode().equals(request.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code invalide");
        }

        u.setVerified(true);
        u.setVerificationCode(null);
        u.setVerificationCodeExpiresAt(null);

        userRepository.save(u);
    }

    private String generate6Digits() {
        int n = ThreadLocalRandom.current().nextInt(0, 1_000_000);
        return String.format("%06d", n);
    }

    /**
     * Génère un nouveau refresh token pour un utilisateur donné.
     *
     * Si un deviceId est fourni, les anciens refresh tokens associés
     * à ce device sont supprimés (rotation par device).
     *
     * @param idUser Identifiant de l'utilisateur
     * @param deviceId Identifiant du device (optionnel)
     * @return La valeur du refresh token généré
     */
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

    /**
     * Valide un refresh token existant et applique une rotation.
     *
     * Le token est supprimé puis remplacé par un nouveau.
     * Si un deviceId est fourni, il doit correspondre à celui enregistré
     * (protection contre le vol de token).
     *
     * @param tokenValue Valeur du refresh token
     * @param deviceId Identifiant du device
     * @return Une paire contenant le nouveau refresh token et un nouveau JWT
     * @throws RuntimeException si le token est invalide, expiré ou ne correspond pas au device
     */
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
