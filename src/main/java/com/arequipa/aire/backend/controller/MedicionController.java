package com.arequipa.aire.backend.controller;

import com.arequipa.aire.backend.dto.CalidadAireDTO;
import com.arequipa.aire.backend.entity.Estacion;
import com.arequipa.aire.backend.entity.Medicion;
import com.arequipa.aire.backend.repository.EstacionRepository;
import com.arequipa.aire.backend.repository.MedicionRepository;
import com.arequipa.aire.backend.util.AQICalculator;
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
 * Controlador REST para gestión de mediciones de calidad del aire.
 */
@RestController
@RequestMapping("/api/mediciones")
@Tag(name = "Mediciones", description = "API para gestión de mediciones de calidad del aire")
@CrossOrigin(origins = "*")
public class MedicionController {

    @Autowired
    private MedicionRepository medicionRepository;

    @Autowired
    private EstacionRepository estacionRepository;

    @Autowired
    private AQICalculator aqiCalculator;

    @Operation(summary = "Obtener todas las mediciones", description = "Devuelve una lista paginada de todas las mediciones")
    @GetMapping
    public ResponseEntity<Page<CalidadAireDTO>> getAllMediciones(
            @Parameter(description = "Número de página")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo por el cual ordenar")
            @RequestParam(defaultValue = "fechaMedicion") String sortBy,
            @Parameter(description = "Dirección del ordenamiento")
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Medicion> mediciones = medicionRepository.findAll(pageable);

        Page<CalidadAireDTO> medicionesDTO = mediciones.map(this::convertToDTO);

        return ResponseEntity.ok(medicionesDTO);
    }

    @Operation(summary = "Obtener mediciones por estación", description = "Devuelve las mediciones de una estación específica")
    @GetMapping("/estacion/{estacionId}")
    public ResponseEntity<Page<CalidadAireDTO>> getMedicionesByEstacion(
            @PathVariable Long estacionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaMedicion").descending());
        // Usar método del repositorio que existe
        Optional<Estacion> estacion = estacionRepository.findById(estacionId);
        if (estacion.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Page<Medicion> mediciones = medicionRepository.findByEstacionAndFechaMedicionBetweenOrderByFechaMedicionDesc(
                estacion.get(), LocalDateTime.now().minusDays(30), LocalDateTime.now(), pageable);

        Page<CalidadAireDTO> medicionesDTO = mediciones.map(this::convertToDTO);

        return ResponseEntity.ok(medicionesDTO);
    }

    @Operation(summary = "Obtener última medición por estación", description = "Devuelve la medición más reciente de una estación")
    @GetMapping("/estacion/{estacionId}/ultima")
    public ResponseEntity<CalidadAireDTO> getUltimaMedicionByEstacion(@PathVariable Long estacionId) {
        Optional<Medicion> medicion = medicionRepository.findLatestByEstacionId(estacionId);
        return medicion.map(m -> ResponseEntity.ok(convertToDTO(m)))
                      .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener mediciones por rango de fechas", description = "Devuelve mediciones en un rango de fechas específico")
    @GetMapping("/rango-fechas")
    public ResponseEntity<List<CalidadAireDTO>> getMedicionesByFechaRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) Long estacionId) {

        List<Medicion> mediciones;
        if (estacionId != null) {
            Pageable pageable = PageRequest.of(0, 1000); // Limitar resultados
            Optional<Estacion> estacion = estacionRepository.findById(estacionId);
            if (estacion.isPresent()) {
                Page<Medicion> medicionesPage = medicionRepository.findByEstacionAndFechaMedicionBetweenOrderByFechaMedicionDesc(
                    estacion.get(), fechaInicio, fechaFin, pageable);
                mediciones = medicionesPage.getContent();
            } else {
                mediciones = List.of();
            }
        } else {
            // Simplificado - usar método básico del repositorio
            mediciones = medicionRepository.findAll().stream()
                    .filter(m -> m.getFechaMedicion().isAfter(fechaInicio) && m.getFechaMedicion().isBefore(fechaFin))
                    .collect(Collectors.toList());
        }

        List<CalidadAireDTO> medicionesDTO = mediciones.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(medicionesDTO);
    }

    @Operation(summary = "Crear nueva medición", description = "Registra una nueva medición de calidad del aire")
    @PostMapping
    public ResponseEntity<CalidadAireDTO> createMedicion(@Valid @RequestBody CalidadAireDTO medicionDTO) {
        Optional<Estacion> estacion = estacionRepository.findById(medicionDTO.getEstacionId());
        if (estacion.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Medicion medicion = convertToEntity(medicionDTO);
        medicion.setEstacion(estacion.get());

        Medicion savedMedicion = medicionRepository.save(medicion);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedMedicion));
    }

    @Operation(summary = "Obtener estadísticas de calidad del aire", description = "Devuelve estadísticas agregadas de calidad del aire")
    @GetMapping("/estadisticas")
    public ResponseEntity<String> getEstadisticas(
            @RequestParam(required = false) Long estacionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        // Placeholder para estadísticas - implementar lógica específica después
        return ResponseEntity.ok("Estadísticas no implementadas aún para el período: " + fechaInicio + " - " + fechaFin);
    }

    @Operation(summary = "Obtener calidad del aire actual", description = "Devuelve la calidad del aire actual de todas las estaciones activas")
    @GetMapping("/actual")
    public ResponseEntity<List<CalidadAireDTO>> getCalidadAireActual() {
        List<Medicion> ultimasMediciones = medicionRepository.findLatestMedicionesPorEstacion();

        List<CalidadAireDTO> calidadActual = ultimasMediciones.stream()
                .map(this::convertToDTO)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        return ResponseEntity.ok(calidadActual);
    }

    /**
     * Convierte una entidad Medicion a CalidadAireDTO
     */
    private CalidadAireDTO convertToDTO(Medicion medicion) {
        CalidadAireDTO dto = new CalidadAireDTO();
        dto.setEstacionId(medicion.getEstacion().getId());
        dto.setEstacionNombre(medicion.getEstacion().getNombre());
        dto.setFechaMedicion(medicion.getFechaMedicion());
        dto.setPm25(medicion.getPm25());
        dto.setPm10(medicion.getPm10());
        dto.setNo2(medicion.getNo2());
        dto.setO3(medicion.getO3());
        dto.setCo(medicion.getCo());
        dto.setSo2(medicion.getSo2());
        dto.setTemperatura(medicion.getTemperatura());
        dto.setHumedad(medicion.getHumedad());
        dto.setPresion(medicion.getPresion());
        dto.setVelocidadViento(medicion.getVelocidadViento());
        dto.setDireccionViento(medicion.getDireccionViento());

        // Calcular AQI básico si hay datos disponibles
        if (medicion.getPm25() != null || medicion.getPm10() != null) {
            Integer aqi = calculateSimpleAQI(medicion.getPm25(), medicion.getPm10());
            dto.setAqi(aqi);
            dto.setCategoriaAqi(getAQICategory(aqi)); // Usar categoriaAqi en lugar de nivelCalidad
        }

        return dto;
    }

    /**
     * Convierte un CalidadAireDTO a entidad Medicion
     */
    private Medicion convertToEntity(CalidadAireDTO dto) {
        Medicion medicion = new Medicion();
        medicion.setFechaMedicion(dto.getFechaMedicion() != null ? dto.getFechaMedicion() : LocalDateTime.now());
        medicion.setPm25(dto.getPm25());
        medicion.setPm10(dto.getPm10());
        medicion.setNo2(dto.getNo2());
        medicion.setO3(dto.getO3());
        medicion.setCo(dto.getCo());
        medicion.setSo2(dto.getSo2());
        medicion.setTemperatura(dto.getTemperatura());
        medicion.setHumedad(dto.getHumedad());
        medicion.setPresion(dto.getPresion());
        medicion.setVelocidadViento(dto.getVelocidadViento());
        medicion.setDireccionViento(dto.getDireccionViento());
        return medicion;
    }

    /**
     * Calcula un AQI simplificado basado en PM2.5 y PM10
     */
    private Integer calculateSimpleAQI(Double pm25, Double pm10) {
        if (pm25 == null && pm10 == null) return null;

        int aqiPm25 = 0;
        int aqiPm10 = 0;

        if (pm25 != null) {
            if (pm25 <= 12.0) aqiPm25 = (int) (50 * pm25 / 12.0);
            else if (pm25 <= 35.4) aqiPm25 = (int) (50 + (50 * (pm25 - 12.1) / 23.3));
            else if (pm25 <= 55.4) aqiPm25 = (int) (100 + (50 * (pm25 - 35.5) / 19.9));
            else if (pm25 <= 150.4) aqiPm25 = (int) (150 + (50 * (pm25 - 55.5) / 94.9));
            else aqiPm25 = Math.min(300, (int) (200 + (100 * (pm25 - 150.5) / 99.9)));
        }

        if (pm10 != null) {
            if (pm10 <= 54) aqiPm10 = (int) (50 * pm10 / 54);
            else if (pm10 <= 154) aqiPm10 = (int) (50 + (50 * (pm10 - 55) / 99));
            else if (pm10 <= 254) aqiPm10 = (int) (100 + (50 * (pm10 - 155) / 99));
            else if (pm10 <= 354) aqiPm10 = (int) (150 + (50 * (pm10 - 255) / 99));
            else aqiPm10 = Math.min(300, (int) (200 + (100 * (pm10 - 355) / 145)));
        }

        return Math.max(aqiPm25, aqiPm10);
    }

    /**
     * Obtiene la categoría de calidad del aire basada en el AQI
     */
    private String getAQICategory(Integer aqi) {
        if (aqi == null) return "Desconocido";
        if (aqi <= 50) return "Bueno";
        if (aqi <= 100) return "Moderado";
        if (aqi <= 150) return "Insalubre para grupos sensibles";
        if (aqi <= 200) return "Insalubre";
        if (aqi <= 300) return "Muy insalubre";
        return "Peligroso";
    }
}
