package com.arequipa.aire.backend.service;

import com.arequipa.aire.backend.dto.AuthResponseDTO;
import com.arequipa.aire.backend.dto.LoginRequestDTO;
import com.arequipa.aire.backend.dto.UsuarioDTO;
import com.arequipa.aire.backend.entity.Usuario;
import com.arequipa.aire.backend.repository.UsuarioRepository;
import com.arequipa.aire.backend.util.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de autenticación.
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AlertasService alertasService;

    /**
     * Autentica un usuario y genera un token JWT.
     */
    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Usuario usuario = (Usuario) authentication.getPrincipal();
            
            // Claims adicionales para el token
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", usuario.getRole().name());
            claims.put("userId", usuario.getId());
            claims.put("tipoSensibilidad", usuario.getTipoSensibilidad().name());
            
            String jwt = jwtUtils.generateToken(usuario, claims);

            // Crear DTO del usuario
            UsuarioDTO usuarioDTO = convertirAUsuarioDTO(usuario);
            
            // Obtener cantidad de alertas no leídas
            usuarioDTO.setAlertasNoLeidas(alertasService.contarAlertasNoLeidas(usuario.getId()));

            logger.info("Usuario autenticado exitosamente: {}", usuario.getUsername());

            return new AuthResponseDTO(jwt, usuarioDTO, jwtUtils.getExpirationTime());

        } catch (Exception e) {
            logger.error("Error en autenticación para usuario: {}", loginRequest.getUsernameOrEmail(), e);
            throw e;
        }
    }

    /**
     * Registra un nuevo usuario.
     */
    public UsuarioDTO registrarUsuario(UsuarioDTO usuarioDTO, String password) {
        // Verificar si ya existe el usuario
        if (usuarioRepository.existsByUsername(usuarioDTO.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear entidad Usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(usuarioDTO.getUsername());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setNombreCompleto(usuarioDTO.getNombreCompleto());
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setRole(usuarioDTO.getRole() != null ? usuarioDTO.getRole() : Usuario.Role.CIUDADANO);
        usuario.setTipoSensibilidad(usuarioDTO.getTipoSensibilidad() != null ? 
                                   usuarioDTO.getTipoSensibilidad() : Usuario.TipoSensibilidad.NORMAL);
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setFechaActualizacion(LocalDateTime.now());

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        logger.info("Usuario registrado exitosamente: {}", usuario.getUsername());

        return convertirAUsuarioDTO(usuarioGuardado);
    }

    /**
     * Obtiene información del usuario actual.
     */
    public UsuarioDTO obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No hay usuario autenticado");
        }

        Usuario usuario = (Usuario) authentication.getPrincipal();
        UsuarioDTO usuarioDTO = convertirAUsuarioDTO(usuario);
        
        // Agregar alertas no leídas
        usuarioDTO.setAlertasNoLeidas(alertasService.contarAlertasNoLeidas(usuario.getId()));
        
        return usuarioDTO;
    }

    /**
     * Actualiza el perfil del usuario actual.
     */
    public UsuarioDTO actualizarPerfil(UsuarioDTO usuarioDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        // Actualizar campos permitidos
        if (usuarioDTO.getNombreCompleto() != null) {
            usuario.setNombreCompleto(usuarioDTO.getNombreCompleto());
        }
        if (usuarioDTO.getTelefono() != null) {
            usuario.setTelefono(usuarioDTO.getTelefono());
        }
        if (usuarioDTO.getTipoSensibilidad() != null) {
            usuario.setTipoSensibilidad(usuarioDTO.getTipoSensibilidad());
        }

        usuario.setFechaActualizacion(LocalDateTime.now());
        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        logger.info("Perfil actualizado para usuario: {}", usuario.getUsername());

        return convertirAUsuarioDTO(usuarioActualizado);
    }

    /**
     * Cambia la contraseña del usuario actual.
     */
    public void cambiarPassword(String passwordActual, String passwordNuevo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        // Verificar contraseña actual
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        // Actualizar contraseña
        usuario.setPassword(passwordEncoder.encode(passwordNuevo));
        usuario.setFechaActualizacion(LocalDateTime.now());
        usuarioRepository.save(usuario);

        logger.info("Contraseña actualizada para usuario: {}", usuario.getUsername());
    }

    /**
     * Convierte una entidad Usuario a DTO.
     */
    private UsuarioDTO convertirAUsuarioDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());
        dto.setNombreCompleto(usuario.getNombreCompleto());
        dto.setTelefono(usuario.getTelefono());
        dto.setRole(usuario.getRole());
        dto.setTipoSensibilidad(usuario.getTipoSensibilidad());
        dto.setActivo(usuario.getActivo());
        dto.setFechaUltimoAcceso(usuario.getFechaUltimoAcceso());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        return dto;
    }

    /**
     * Valida que un usuario existe y está activo.
     */
    public boolean validarUsuario(String usernameOrEmail) {
        return usuarioRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .map(Usuario::getActivo)
                .orElse(false);
    }
}