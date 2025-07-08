package com.arequipa.aire.backend.dto;

import com.arequipa.aire.backend.entity.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO para usuarios.
 */
@Schema(description = "Información del usuario")
public class UsuarioDTO {

    @Schema(description = "ID del usuario", example = "1")
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nombre de usuario", example = "juan_perez", required = true)
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Schema(description = "Correo electrónico", example = "juan.perez@email.com", required = true)
    private String email;

    @Schema(description = "Nombre completo", example = "Juan Pérez García")
    private String nombreCompleto;

    @Schema(description = "Teléfono", example = "+51 987654321")
    private String telefono;

    @Schema(description = "Rol del usuario", example = "CIUDADANO")
    private Usuario.Role role;

    @Schema(description = "Tipo de sensibilidad", example = "NORMAL")
    private Usuario.TipoSensibilidad tipoSensibilidad;

    @Schema(description = "Usuario activo", example = "true")
    private Boolean activo;

    @Schema(description = "Fecha del último acceso")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaUltimoAcceso;

    @Schema(description = "Fecha de registro")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Cantidad de alertas no leídas", example = "3")
    private Long alertasNoLeidas;

    // Constructors
    public UsuarioDTO() {}

    public UsuarioDTO(Long id, String username, String email, String nombreCompleto, 
                     Usuario.Role role, Usuario.TipoSensibilidad tipoSensibilidad, Boolean activo) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.nombreCompleto = nombreCompleto;
        this.role = role;
        this.tipoSensibilidad = tipoSensibilidad;
        this.activo = activo;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Usuario.Role getRole() {
        return role;
    }

    public void setRole(Usuario.Role role) {
        this.role = role;
    }

    public Usuario.TipoSensibilidad getTipoSensibilidad() {
        return tipoSensibilidad;
    }

    public void setTipoSensibilidad(Usuario.TipoSensibilidad tipoSensibilidad) {
        this.tipoSensibilidad = tipoSensibilidad;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaUltimoAcceso() {
        return fechaUltimoAcceso;
    }

    public void setFechaUltimoAcceso(LocalDateTime fechaUltimoAcceso) {
        this.fechaUltimoAcceso = fechaUltimoAcceso;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getAlertasNoLeidas() {
        return alertasNoLeidas;
    }

    public void setAlertasNoLeidas(Long alertasNoLeidas) {
        this.alertasNoLeidas = alertasNoLeidas;
    }
}