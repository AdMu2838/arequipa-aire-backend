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

    @Operation(summary = "Obtener alertas activas", description = "Devuelve las alertas que están activas/no leídas")
    @GetMapping("/activas")
    public ResponseEntity<List<AlertaDTO>> getAlertasActivas() {
        List<Alerta> alertas = alertaRepository.findAll().stream()
                .filter(a -> !a.getLeida())
                .sorted((a1, a2) -> a2.getFechaCreacion().compareTo(a1.getFechaCreacion()))
                .collect(Collectors.toList());
        List<AlertaDTO> alertasDTO = alertas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(alertasDTO);
    }

    @Operation(summary = "Obtener alertas por nivel", description = "Devuelve las alertas de un nivel de severidad específico")
    @GetMapping("/nivel/{nivel}")
    public ResponseEntity<List<AlertaDTO>> getAlertasByNivel(@PathVariable String nivel) {
        try {
            Alerta.SeveridadAlerta severidadAlerta = Alerta.SeveridadAlerta.valueOf(nivel.toUpperCase());
            List<Alerta> alertas = alertaRepository.findAll().stream()
                    .filter(a -> a.getSeveridad() == severidadAlerta)
                    .sorted((a1, a2) -> a2.getFechaCreacion().compareTo(a1.getFechaCreacion()))
                    .collect(Collectors.toList());
            List<AlertaDTO> alertasDTO = alertas.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(alertasDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener alerta por ID", description = "Devuelve una alerta específica por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<AlertaDTO> getAlertaById(@PathVariable Long id) {
        Optional<Alerta> alerta = alertaRepository.findById(id);
        return alerta.map(a -> ResponseEntity.ok(convertToDTO(a)))
                    .orElse(ResponseEntity.notFound().build());
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

    @Operation(summary = "Actualizar alerta", description = "Actualiza una alerta existente")
    @PutMapping("/{id}")
    public ResponseEntity<AlertaDTO> updateAlerta(@PathVariable Long id, @Valid @RequestBody AlertaDTO alertaDTO) {
        Optional<Alerta> existingAlerta = alertaRepository.findById(id);
        if (existingAlerta.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Alerta alerta = existingAlerta.get();
        alerta.setTitulo(alertaDTO.getTitulo());
        alerta.setMensaje(alertaDTO.getMensaje());
        alerta.setTipo(alertaDTO.getTipo());
        alerta.setSeveridad(alertaDTO.getSeveridad());
        alerta.setValorMedido(alertaDTO.getValorMedido());
        alerta.setUmbralConfigurado(alertaDTO.getUmbralConfigurado());
        alerta.setLeida(alertaDTO.getLeida());

        Alerta updatedAlerta = alertaRepository.save(alerta);
        return ResponseEntity.ok(convertToDTO(updatedAlerta));
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
