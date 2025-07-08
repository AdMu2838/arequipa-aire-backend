package com.arequipa.aire.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para la solicitud de login.
 */
@Schema(description = "Solicitud de autenticación")
public class LoginRequestDTO {

    @NotBlank(message = "El nombre de usuario o email es obligatorio")
    @Schema(description = "Nombre de usuario o email", example = "juan_perez", required = true)
    private String usernameOrEmail;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Schema(description = "Contraseña", example = "password123", required = true)
    private String password;

    // Constructors
    public LoginRequestDTO() {}

    public LoginRequestDTO(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    // Getters and Setters
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}