package com.humanbooster.cda.plugzy.controller;

import com.humanbooster.cda.plugzy.controller.dto.auth.LoginCredentialsDTO;
import com.humanbooster.cda.plugzy.controller.dto.auth.LoginResponseDTO;
import com.humanbooster.cda.plugzy.controller.dto.common.SimpleMessageDTO;
import com.humanbooster.cda.plugzy.entity.User;
import com.humanbooster.cda.plugzy.security.AuthService;
import com.humanbooster.cda.plugzy.security.TokenPair;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController {

    private static final String DEVICE_HEADER = "X-Device-Id";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody @Valid LoginCredentialsDTO credentials,
            @RequestHeader(name = DEVICE_HEADER, required = false) String deviceId
    ) {
        LoginResponseDTO responseDto = authService.login(credentials);

        // Refresh token (cookie httpOnly)
        String refreshToken = authService.generateRefreshToken(
                responseDto.getUser().getId().toString(),
                deviceId
        );

        ResponseCookie refreshCookie = generateRefreshCookie(refreshToken);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(responseDto);
    }

    @PostMapping("/api/refresh-token")
    public ResponseEntity<SimpleMessageDTO> refreshToken(
            @CookieValue(name = "refresh-token", required = false) String token,
            @RequestHeader(name = "X-Refresh-Required", required = false) String refreshRequired,
            @RequestHeader(name = DEVICE_HEADER, required = false) String deviceId
    ) {
        boolean strict = "true".equalsIgnoreCase(refreshRequired);

        if (token == null) {
            if (strict) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Refresh token missing");
            }
            return ResponseEntity.noContent().build(); // utilisateur anonyme
        }

        try {
            TokenPair tokens = authService.validateRefreshToken(token, deviceId);
            ResponseCookie refreshCookie = generateRefreshCookie(tokens.getRefreshToken());

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(new SimpleMessageDTO(tokens.getJwt()));

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token");
        }
    }

    @PostMapping("/api/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh-token", required = false) String token,
            @RequestHeader(name = DEVICE_HEADER, required = false) String deviceId
    ) {
        if (token != null) {
            authService.logout(token, deviceId);
        }

        ResponseCookie deleteCookie = ResponseCookie.from("refresh-token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite(SameSiteCookies.LAX.toString())
                .path("/api/refresh-token")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    @GetMapping("/api/protected")
    public SimpleMessageDTO protectedEndpoint(@AuthenticationPrincipal User user) {
        return new SimpleMessageDTO("Authenticated as " + user.getEmail());
    }

    private ResponseCookie generateRefreshCookie(String refreshToken) {
        return ResponseCookie.from("refresh-token", refreshToken)
                .httpOnly(true)
                .secure(false) // true en prod (HTTPS)
                .sameSite(SameSiteCookies.LAX.toString())
                .path("/api/refresh-token")
                .build();
    }
}
