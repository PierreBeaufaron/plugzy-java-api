package com.humanbooster.cda.plugzy.security;

import com.humanbooster.cda.plugzy.controller.dto.auth.LoginCredentialsDTO;
import com.humanbooster.cda.plugzy.controller.dto.auth.LoginResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginCredentialsDTO credentials);

    String generateRefreshToken(String userId);

    TokenPair validateRefreshToken(String token);

    void logout(String refreshToken);
}
