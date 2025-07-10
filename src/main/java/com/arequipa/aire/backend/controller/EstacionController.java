package com.arequipa.aire.backend.controller;

import com.arequipa.aire.backend.dto.EstacionDTO;
import com.arequipa.aire.backend.entity.Estacion;
import com.arequipa.aire.backend.repository.EstacionRepository;
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
 * Controlador REST para gestión de estaciones de monitoreo.
 */
@RestController
@RequestMapping("/api/estaciones")
@Tag(name = "Estaciones", description = "API para gestión de estaciones de monitoreo")
@CrossOrigin(origins = "*")
public class EstacionController {

    @Autowired
    private EstacionRepository estacionRepository;

    @Operation(summary = "Obtener todas las estaciones", description = "Devuelve una lista paginada de todas las estaciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de estaciones obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<Page<EstacionDTO>> getAllEstaciones(
            @Parameter(description = "Número de página (empezando desde 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo por el cual ordenar")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Dirección del ordenamiento (asc/desc)")
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Estacion> estaciones = estacionRepository.findAll(pageable);

        Page<EstacionDTO> estacionesDTO = estaciones.map(this::convertToDTO);

        return ResponseEntity.ok(estacionesDTO);
    }

    @Operation(summary = "Obtener estaciones activas", description = "Devuelve solo las estaciones que están activas")
    @GetMapping("/activas")
    public ResponseEntity<List<EstacionDTO>> getEstacionesActivas() {
        List<Estacion> estaciones = estacionRepository.findByActivaTrue();
        List<EstacionDTO> estacionesDTO = estaciones.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estacionesDTO);
    }

    @Operation(summary = "Obtener estación por ID", description = "Devuelve una estación específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estación encontrada"),
            @ApiResponse(responseCode = "404", description = "Estación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EstacionDTO> getEstacionById(@PathVariable Long id) {
        Optional<Estacion> estacion = estacionRepository.findById(id);
        if (estacion.isPresent()) {
            return ResponseEntity.ok(convertToDTO(estacion.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear nueva estación", description = "Crea una nueva estación de monitoreo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Estación creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<EstacionDTO> createEstacion(@Valid @RequestBody EstacionDTO estacionDTO) {
        Estacion estacion = convertToEntity(estacionDTO);
        estacion.setFechaCreacion(LocalDateTime.now());
        estacion.setFechaActualizacion(LocalDateTime.now());

        Estacion savedEstacion = estacionRepository.save(estacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedEstacion));
    }

    @Operation(summary = "Actualizar estación", description = "Actualiza una estación existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estación actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Estación no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EstacionDTO> updateEstacion(@PathVariable Long id, @Valid @RequestBody EstacionDTO estacionDTO) {
        Optional<Estacion> existingEstacion = estacionRepository.findById(id);
        if (!existingEstacion.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Estacion estacion = existingEstacion.get();
        estacion.setNombre(estacionDTO.getNombre());
        estacion.setDescripcion(estacionDTO.getDescripcion());
        estacion.setLatitud(estacionDTO.getLatitud());
        estacion.setLongitud(estacionDTO.getLongitud());
        estacion.setDistrito(estacionDTO.getDistrito());
        estacion.setActiva(estacionDTO.getActiva());
        estacion.setFechaActualizacion(LocalDateTime.now());

        Estacion updatedEstacion = estacionRepository.save(estacion);
        return ResponseEntity.ok(convertToDTO(updatedEstacion));
    }

    @Operation(summary = "Eliminar estación", description = "Elimina una estación (eliminación lógica)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Estación eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Estación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstacion(@PathVariable Long id) {
        Optional<Estacion> estacion = estacionRepository.findById(id);
        if (!estacion.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // Eliminación lógica
        Estacion estacionToUpdate = estacion.get();
        estacionToUpdate.setActiva(false);
        estacionToUpdate.setFechaActualizacion(LocalDateTime.now());
        estacionRepository.save(estacionToUpdate);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar estaciones por distrito", description = "Busca estaciones en un distrito específico")
    @GetMapping("/distrito/{distrito}")
    public ResponseEntity<List<EstacionDTO>> getEstacionesByDistrito(@PathVariable String distrito) {
        List<Estacion> estaciones = estacionRepository.findByDistritoAndActivaTrue(distrito);
        List<EstacionDTO> estacionesDTO = estaciones.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estacionesDTO);
    }

    /**
     * Convierte una entidad Estacion a EstacionDTO
     */
    private EstacionDTO convertToDTO(Estacion estacion) {
        EstacionDTO dto = new EstacionDTO();
        dto.setId(estacion.getId());
        dto.setNombre(estacion.getNombre());
        dto.setDescripcion(estacion.getDescripcion());
        dto.setLatitud(estacion.getLatitud());
        dto.setLongitud(estacion.getLongitud());
        dto.setDistrito(estacion.getDistrito());
        dto.setActiva(estacion.getActiva());
        dto.setFechaInstalacion(estacion.getFechaInstalacion());
        // Removido setFechaCreacion y setFechaActualizacion ya que no existen en EstacionDTO
        return dto;
    }

    /**
     * Convierte un EstacionDTO a entidad Estacion
     */
    private Estacion convertToEntity(EstacionDTO dto) {
        Estacion estacion = new Estacion();
        estacion.setNombre(dto.getNombre());
        estacion.setDescripcion(dto.getDescripcion());
        estacion.setLatitud(dto.getLatitud());
        estacion.setLongitud(dto.getLongitud());
        estacion.setDistrito(dto.getDistrito());
        estacion.setActiva(dto.getActiva() != null ? dto.getActiva() : true);
        estacion.setFechaInstalacion(dto.getFechaInstalacion());
        return estacion;
    }
}
