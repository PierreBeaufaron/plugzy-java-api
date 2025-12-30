package com.humanbooster.cda.plugzy.controller.dto.user;

import java.util.UUID;

public class UserPublicDTO {

    private UUID id;
    private String email;
    private String role;

    public UserPublicDTO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
