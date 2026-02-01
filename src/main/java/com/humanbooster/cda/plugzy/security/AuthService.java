package com.humanbooster.cda.plugzy.security;

import com.humanbooster.cda.plugzy.controller.dto.auth.*;

public interface AuthService {

    LoginResponseDTO login(LoginCredentialsDTO credentials);

    RegisterResponseDTO register(RegisterRequestDTO request);

    void verify(VerifyRequestDTO request);

    String generateRefreshToken(String userId, String deviceId);

    TokenPair validateRefreshToken(String token, String deviceId);

    void logout(String refreshToken, String deviceId);
}
