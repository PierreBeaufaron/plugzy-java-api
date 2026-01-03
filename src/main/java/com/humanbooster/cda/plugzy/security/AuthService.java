package com.humanbooster.cda.plugzy.security;

import com.humanbooster.cda.plugzy.controller.dto.auth.LoginCredentialsDTO;
import com.humanbooster.cda.plugzy.controller.dto.auth.LoginResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginCredentialsDTO credentials);

    String generateRefreshToken(String userId, String deviceId);

    TokenPair validateRefreshToken(String token, String deviceId);

    void logout(String refreshToken, String deviceId);
}
