package com.arequipa.aire.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para la respuesta de autenticación.
 */
@Schema(description = "Respuesta de autenticación")
public class AuthResponseDTO {

    @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Información del usuario")
    private UsuarioDTO usuario;

    @Schema(description = "Tiempo de expiración del token en segundos", example = "86400")
    private Long expiresIn;

    // Constructors
    public AuthResponseDTO() {}

    public AuthResponseDTO(String accessToken, UsuarioDTO usuario, Long expiresIn) {
        this.accessToken = accessToken;
        this.usuario = usuario;
        this.expiresIn = expiresIn;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}