package com.humanbooster.cda.plugzy.controller.dto.auth;

import com.humanbooster.cda.plugzy.controller.dto.user.UserPublicDTO;

public class LoginResponseDTO {

    private String token;
    private UserPublicDTO user;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, UserPublicDTO user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserPublicDTO getUser() {
        return user;
    }

    public void setUser(UserPublicDTO user) {
        this.user = user;
    }
}
