package com.humanbooster.cda.plugzy.security.jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class JwtUtil {

    private final UserDetailsService userService;
    private final KeyManager keyManager;

    // Injection par constructeur
    public JwtUtil(UserDetailsService userService, KeyManager keyManager) {
        this.userService = userService;
        this.keyManager = keyManager;
    }

    /** JWT par défaut : 30 minutes */
    public String generateToken(UserDetails user) {
        return generateToken(user, Instant.now().plus(30, ChronoUnit.MINUTES));
    }

    /** JWT avec expiration personnalisée */
    public String generateToken(UserDetails user, Instant expiration) {
        return JWT.create()
                .withSubject(user.getUsername()) // email chez toi
                .withExpiresAt(expiration)
                .sign(keyManager.getAlgorithm());
    }

    /** Vérifie le token et retourne le User associé */
    public UserDetails validateToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT
                    .require(keyManager.getAlgorithm())
                    .build()
                    .verify(token);

            String email = decodedJWT.getSubject();
            return userService.loadUserByUsername(email);

        } catch (JWTVerificationException | UsernameNotFoundException e) {
            throw new AuthorizationDeniedException("Error verifying token");
        }
    }
}
