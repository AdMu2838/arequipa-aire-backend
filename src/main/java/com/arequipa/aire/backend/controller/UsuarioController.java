package com.arequipa.aire.backend.controller;

import com.arequipa.aire.backend.dto.UsuarioDTO;
import com.arequipa.aire.backend.entity.Usuario;
import com.arequipa.aire.backend.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "API para gestión de usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Usuario o email ya existe")
    })
    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registrarUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        // Verificar si el usuario ya existe
        if (usuarioRepository.existsByUsername(usuarioDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Usuario usuario = convertToEntity(usuarioDTO);
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setFechaActualizacion(LocalDateTime.now());

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedUsuario));
    }

    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve una lista paginada de todos los usuarios")
    @GetMapping
    public ResponseEntity<Page<UsuarioDTO>> getAllUsuarios(
            @Parameter(description = "Número de página")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo por el cual ordenar")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Dirección del ordenamiento")
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Usuario> usuarios = usuarioRepository.findAll(pageable);

        Page<UsuarioDTO> usuariosDTO = usuarios.map(this::convertToDTO);

        return ResponseEntity.ok(usuariosDTO);
    }

    @Operation(summary = "Obtener usuario por ID", description = "Devuelve un usuario específico por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(u -> ResponseEntity.ok(convertToDTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> updateUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioDTO usuarioDTO) {
        Optional<Usuario> existingUsuario = usuarioRepository.findById(id);
        if (existingUsuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = existingUsuario.get();

        // Verificar si el nuevo username o email ya existen (excepto para el usuario actual)
        if (!usuario.getUsername().equals(usuarioDTO.getUsername()) &&
                usuarioRepository.existsByUsername(usuarioDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (!usuario.getEmail().equals(usuarioDTO.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        updateUsuarioFromDTO(usuario, usuarioDTO);
        usuario.setFechaActualizacion(LocalDateTime.now());

        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.ok(convertToDTO(updatedUsuario));
    }

    @Operation(summary = "Desactivar usuario", description = "Desactiva un usuario (eliminación lógica)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarUsuario(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuarioToUpdate = usuario.get();
        usuarioToUpdate.setActivo(false);
        usuarioToUpdate.setFechaActualizacion(LocalDateTime.now());
        usuarioRepository.save(usuarioToUpdate);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar usuarios por rol", description = "Busca usuarios por rol específico")
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioDTO>> getUsuariosByRol(@PathVariable String rol) {
        try {
            Usuario.Role rolEnum = Usuario.Role.valueOf(rol.toUpperCase());
            // Simplificar - filtrar en memoria ya que findByRole no existe
            List<Usuario> todosUsuarios = usuarioRepository.findAll();
            List<Usuario> usuarios = todosUsuarios.stream()
                    .filter(u -> u.getRole().equals(rolEnum))
                    .collect(Collectors.toList());

            List<UsuarioDTO> usuariosDTO = usuarios.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(usuariosDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener usuarios activos", description = "Devuelve solo los usuarios que están activos")
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioDTO>> getUsuariosActivos() {
        List<Usuario> usuarios = usuarioRepository.findByActivoTrue();
        List<UsuarioDTO> usuariosDTO = usuarios.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuariosDTO);
    }

    /**
     * Convierte una entidad Usuario a UsuarioDTO
     */
    private UsuarioDTO convertToDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());
        dto.setNombreCompleto(usuario.getNombreCompleto());
        dto.setTelefono(usuario.getTelefono());
        dto.setRole(usuario.getRole());
        dto.setTipoSensibilidad(usuario.getTipoSensibilidad());
        dto.setActivo(usuario.getActivo());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        return dto;
    }

    /**
     * Convierte un UsuarioDTO a entidad Usuario
     */
    private Usuario convertToEntity(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setEmail(dto.getEmail());
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRole(dto.getRole() != null ? dto.getRole() : Usuario.Role.CIUDADANO);

        if (dto.getTipoSensibilidad() != null) {
            usuario.setTipoSensibilidad(dto.getTipoSensibilidad());
        }

        usuario.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return usuario;
    }

    /**
     * Actualiza una entidad Usuario con datos del DTO
     */
    private void updateUsuarioFromDTO(Usuario usuario, UsuarioDTO dto) {
        usuario.setUsername(dto.getUsername());
        usuario.setEmail(dto.getEmail());
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setTelefono(dto.getTelefono());

        if (dto.getRole() != null) {
            usuario.setRole(dto.getRole());
        }

        if (dto.getTipoSensibilidad() != null) {
            usuario.setTipoSensibilidad(dto.getTipoSensibilidad());
        }

        if (dto.getActivo() != null) {
            usuario.setActivo(dto.getActivo());
        }
    }
}
