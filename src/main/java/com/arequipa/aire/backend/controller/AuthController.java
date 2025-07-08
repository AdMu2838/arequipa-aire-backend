package com.arequipa.aire.backend.controller;

import com.arequipa.aire.backend.dto.AuthResponseDTO;
import com.arequipa.aire.backend.dto.LoginRequestDTO;
import com.arequipa.aire.backend.dto.UsuarioDTO;
import com.arequipa.aire.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para autenticación y gestión de usuarios.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints para autenticación y gestión de usuarios")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint para login de usuarios.
     */
    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario y devuelve un token JWT"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciales inválidas"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos"
        )
    })
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para registro de nuevos usuarios.
     */
    @PostMapping("/registro")
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea una nueva cuenta de usuario"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuario registrado exitosamente",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos o usuario ya existe"
        )
    })
    public ResponseEntity<UsuarioDTO> registro(
            @Valid @RequestBody RegistroRequestDTO registroRequest) {
        
        UsuarioDTO usuario = authService.registrarUsuario(registroRequest.getUsuario(), registroRequest.getPassword());
        return ResponseEntity.status(201).body(usuario);
    }

    /**
     * Endpoint para obtener información del usuario actual.
     */
    @GetMapping("/me")
    @Operation(
        summary = "Obtener perfil del usuario actual",
        description = "Devuelve la información del usuario autenticado",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Información del usuario",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @PreAuthorize("hasRole('CIUDADANO') or hasRole('AUTORIDAD') or hasRole('ADMIN')")
    public ResponseEntity<UsuarioDTO> obtenerPerfil() {
        UsuarioDTO usuario = authService.obtenerUsuarioActual();
        return ResponseEntity.ok(usuario);
    }

    /**
     * Endpoint para actualizar el perfil del usuario.
     */
    @PutMapping("/perfil")
    @Operation(
        summary = "Actualizar perfil del usuario",
        description = "Actualiza la información del perfil del usuario autenticado",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Perfil actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @PreAuthorize("hasRole('CIUDADANO') or hasRole('AUTORIDAD') or hasRole('ADMIN')")
    public ResponseEntity<UsuarioDTO> actualizarPerfil(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO usuarioActualizado = authService.actualizarPerfil(usuarioDTO);
        return ResponseEntity.ok(usuarioActualizado);
    }

    /**
     * Endpoint para cambiar contraseña.
     */
    @PostMapping("/cambiar-password")
    @Operation(
        summary = "Cambiar contraseña",
        description = "Permite al usuario cambiar su contraseña",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Contraseña cambiada exitosamente"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Contraseña actual incorrecta o datos inválidos"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @PreAuthorize("hasRole('CIUDADANO') or hasRole('AUTORIDAD') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponseDTO> cambiarPassword(@Valid @RequestBody CambiarPasswordDTO request) {
        authService.cambiarPassword(request.getPasswordActual(), request.getPasswordNuevo());
        return ResponseEntity.ok(new MessageResponseDTO("Contraseña actualizada exitosamente"));
    }

    /**
     * DTO para solicitud de registro.
     */
    public static class RegistroRequestDTO {
        @Valid
        private UsuarioDTO usuario;
        
        @Schema(description = "Contraseña del usuario", minLength = 8, example = "password123")
        private String password;

        // Getters and Setters
        public UsuarioDTO getUsuario() { return usuario; }
        public void setUsuario(UsuarioDTO usuario) { this.usuario = usuario; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    /**
     * DTO para cambio de contraseña.
     */
    public static class CambiarPasswordDTO {
        @Schema(description = "Contraseña actual", example = "oldpassword")
        private String passwordActual;
        
        @Schema(description = "Nueva contraseña", minLength = 8, example = "newpassword123")
        private String passwordNuevo;

        // Getters and Setters
        public String getPasswordActual() { return passwordActual; }
        public void setPasswordActual(String passwordActual) { this.passwordActual = passwordActual; }
        public String getPasswordNuevo() { return passwordNuevo; }
        public void setPasswordNuevo(String passwordNuevo) { this.passwordNuevo = passwordNuevo; }
    }

    /**
     * DTO para respuestas con mensaje.
     */
    public static class MessageResponseDTO {
        private String message;

        public MessageResponseDTO(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}