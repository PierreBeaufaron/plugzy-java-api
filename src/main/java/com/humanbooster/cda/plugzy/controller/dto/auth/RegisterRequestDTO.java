package com.humanbooster.cda.plugzy.controller.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 6, max = 80, message = "Le nom d'utilisateur doit contenir entre 6 et 80 caractères")
    private String username;
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Le format de l'email est invalide")
    @Size(min = 6, max = 80, message = "L'email doit contenir entre 6 et 80 caractères")
    private String email;
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(
            regexp = "^\\+?[0-9 ]{8,15}$",
            message = "Le numéro de téléphone doit contenir entre 8 et 15 chiffres"
    )
    private String phone;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
