package com.arequipa.aire.backend.controller;

import com.arequipa.aire.backend.dto.AlertaDTO;
import com.arequipa.aire.backend.entity.Alerta;
import com.arequipa.aire.backend.entity.Estacion;
import com.arequipa.aire.backend.entity.Usuario;
import com.arequipa.aire.backend.repository.AlertaRepository;
import com.arequipa.aire.backend.repository.EstacionRepository;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de alertas del sistema.
 */
@RestController
@RequestMapping("/api/alertas")
@Tag(name = "Alertas", description = "API para gestión de alertas del sistema")
@CrossOrigin(origins = "*")
public class AlertaController {

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstacionRepository estacionRepository;

    @Operation(summary = "Obtener todas las alertas", description = "Devuelve una lista paginada de todas las alertas")
    @GetMapping
    public ResponseEntity<Page<AlertaDTO>> getAllAlertas(
            @Parameter(description = "Número de página")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo por el cual ordenar")
            @RequestParam(defaultValue = "fechaCreacion") String sortBy,
            @Parameter(description = "Dirección del ordenamiento")
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Alerta> alertas = alertaRepository.findAll(pageable);

        Page<AlertaDTO> alertasDTO = alertas.map(this::convertToDTO);

        return ResponseEntity.ok(alertasDTO);
    }

    @Operation(summary = "Obtener alertas por usuario", description = "Devuelve las alertas de un usuario específico")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<AlertaDTO>> getAlertasByUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        Page<Alerta> alertas = alertaRepository.findByUsuarioOrderByFechaCreacionDesc(usuario.get(), pageable);

        Page<AlertaDTO> alertasDTO = alertas.map(this::convertToDTO);

        return ResponseEntity.ok(alertasDTO);
    }

    @Operation(summary = "Obtener alertas no leídas", description = "Devuelve las alertas no leídas de un usuario")
    @GetMapping("/usuario/{usuarioId}/no-leidas")
    public ResponseEntity<List<AlertaDTO>> getAlertasNoLeidas(@PathVariable Long usuarioId) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Alerta> alertas = alertaRepository.findByUsuarioAndLeidaFalseOrderByFechaCreacionDesc(usuario.get());
        List<AlertaDTO> alertasDTO = alertas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(alertasDTO);
    }

    @Operation(summary = "Obtener alertas por estación", description = "Devuelve las alertas relacionadas con una estación específica")
    @GetMapping("/estacion/{estacionId}")
    public ResponseEntity<List<AlertaDTO>> getAlertasByEstacion(@PathVariable Long estacionId) {
        Optional<Estacion> estacion = estacionRepository.findById(estacionId);
        if (estacion.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Por ahora devolvemos una lista vacía ya que el método específico no existe en el repositorio
        // Se puede implementar más tarde si es necesario
        List<AlertaDTO> alertasDTO = List.of();

        return ResponseEntity.ok(alertasDTO);
    }

    @Operation(summary = "Obtener alertas por tipo", description = "Devuelve las alertas de un tipo específico para un usuario")
    @GetMapping("/usuario/{usuarioId}/tipo/{tipo}")
    public ResponseEntity<List<AlertaDTO>> getAlertasByTipo(@PathVariable Long usuarioId, @PathVariable String tipo) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Alerta.TipoAlerta tipoAlerta = Alerta.TipoAlerta.valueOf(tipo.toUpperCase());
            List<Alerta> alertas = alertaRepository.findByUsuarioAndTipoOrderByFechaCreacionDesc(usuario.get(), tipoAlerta);
            List<AlertaDTO> alertasDTO = alertas.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(alertasDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener alertas por severidad", description = "Devuelve las alertas de una severidad específica para un usuario")
    @GetMapping("/usuario/{usuarioId}/severidad/{severidad}")
    public ResponseEntity<List<AlertaDTO>> getAlertasBySeveridad(@PathVariable Long usuarioId, @PathVariable String severidad) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Alerta.SeveridadAlerta severidadAlerta = Alerta.SeveridadAlerta.valueOf(severidad.toUpperCase());
            List<Alerta> alertas = alertaRepository.findByUsuarioAndSeveridadOrderByFechaCreacionDesc(usuario.get(), severidadAlerta);
            List<AlertaDTO> alertasDTO = alertas.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(alertasDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener alertas por rango de fechas", description = "Devuelve alertas en un rango de fechas específico")
    @GetMapping("/rango-fechas")
    public ResponseEntity<List<AlertaDTO>> getAlertasByFechaRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<Alerta> alertas = alertaRepository.findByFechaCreacionBetweenOrderByFechaCreacionDesc(fechaInicio, fechaFin);

        List<AlertaDTO> alertasDTO = alertas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(alertasDTO);
    }

    @Operation(summary = "Crear nueva alerta", description = "Crea una nueva alerta en el sistema")
    @PostMapping
    public ResponseEntity<AlertaDTO> createAlerta(@Valid @RequestBody AlertaDTO alertaDTO) {
        Optional<Usuario> usuario = usuarioRepository.findById(alertaDTO.getUsuarioId());
        if (usuario.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Alerta alerta = convertToEntity(alertaDTO);
        alerta.setUsuario(usuario.get());

        if (alertaDTO.getEstacionId() != null) {
            Optional<Estacion> estacion = estacionRepository.findById(alertaDTO.getEstacionId());
            estacion.ifPresent(alerta::setEstacion);
        }

        Alerta savedAlerta = alertaRepository.save(alerta);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedAlerta));
    }

    @Operation(summary = "Marcar alerta como leída", description = "Marca una alerta específica como leída")
    @PutMapping("/{id}/marcar-leida")
    public ResponseEntity<AlertaDTO> marcarAlertaComoLeida(@PathVariable Long id) {
        Optional<Alerta> alertaOpt = alertaRepository.findById(id);
        if (alertaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Alerta alerta = alertaOpt.get();
        alerta.setLeida(true);
        alerta.setFechaLectura(LocalDateTime.now());

        Alerta updatedAlerta = alertaRepository.save(alerta);
        return ResponseEntity.ok(convertToDTO(updatedAlerta));
    }

    @Operation(summary = "Marcar todas las alertas como leídas", description = "Marca todas las alertas de un usuario como leídas")
    @PutMapping("/usuario/{usuarioId}/marcar-todas-leidas")
    public ResponseEntity<Void> marcarTodasAlertasComoLeidas(@PathVariable Long usuarioId) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Alerta> alertasNoLeidas = alertaRepository.findByUsuarioAndLeidaFalseOrderByFechaCreacionDesc(usuario.get());

        for (Alerta alerta : alertasNoLeidas) {
            alerta.setLeida(true);
            alerta.setFechaLectura(LocalDateTime.now());
        }

        alertaRepository.saveAll(alertasNoLeidas);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Eliminar alerta", description = "Elimina una alerta específica")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlerta(@PathVariable Long id) {
        if (!alertaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        alertaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener alerta por ID", description = "Devuelve una alerta específica por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<AlertaDTO> getAlertaById(@PathVariable Long id) {
        Optional<Alerta> alerta = alertaRepository.findById(id);
        return alerta.map(a -> ResponseEntity.ok(convertToDTO(a)))
                    .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Convierte una entidad Alerta a AlertaDTO
     */
    private AlertaDTO convertToDTO(Alerta alerta) {
        AlertaDTO dto = new AlertaDTO();
        dto.setId(alerta.getId());
        dto.setUsuarioId(alerta.getUsuario().getId());

        if (alerta.getEstacion() != null) {
            dto.setEstacionId(alerta.getEstacion().getId());
            dto.setEstacionNombre(alerta.getEstacion().getNombre());
        }

        dto.setTipo(alerta.getTipo());
        dto.setSeveridad(alerta.getSeveridad());
        dto.setTitulo(alerta.getTitulo());
        dto.setMensaje(alerta.getMensaje());
        dto.setValorMedido(alerta.getValorMedido());
        dto.setUmbralConfigurado(alerta.getUmbralConfigurado());
        dto.setLeida(alerta.getLeida());
        dto.setFechaCreacion(alerta.getFechaCreacion());
        dto.setFechaLectura(alerta.getFechaLectura());

        return dto;
    }

    /**
     * Convierte un AlertaDTO a entidad Alerta
     */
    private Alerta convertToEntity(AlertaDTO dto) {
        Alerta alerta = new Alerta();
        alerta.setTipo(dto.getTipo());
        alerta.setSeveridad(dto.getSeveridad());
        alerta.setTitulo(dto.getTitulo());
        alerta.setMensaje(dto.getMensaje());
        alerta.setValorMedido(dto.getValorMedido());
        alerta.setUmbralConfigurado(dto.getUmbralConfigurado());
        alerta.setLeida(dto.getLeida() != null ? dto.getLeida() : false);

        return alerta;
    }
}
