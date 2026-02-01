package com.humanbooster.cda.plugzy.controller.dto.auth;

import java.util.UUID;

public class RegisterResponseDTO {
    private UUID id;
    private String email;
    private boolean verified;

    public RegisterResponseDTO(UUID id, String email, boolean verified) {
        this.id = id;
        this.email = email;
        this.verified = verified;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public boolean isVerified() { return verified; }
}
