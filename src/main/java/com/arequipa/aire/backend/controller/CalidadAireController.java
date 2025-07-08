package com.arequipa.aire.backend.controller;

import com.arequipa.aire.backend.dto.CalidadAireDTO;
import com.arequipa.aire.backend.service.CalidadAireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador para endpoints de calidad del aire.
 */
@RestController
@RequestMapping("/calidad-aire")
@Tag(name = "Calidad del Aire", description = "Endpoints para consultar datos de calidad del aire")
public class CalidadAireController {

    @Autowired
    private CalidadAireService calidadAireService;

    /**
     * Obtiene datos actuales de todas las estaciones (endpoint público).
     */
    @GetMapping("/actual")
    @Operation(
        summary = "Obtener datos actuales de calidad del aire",
        description = "Devuelve las mediciones más recientes de todas las estaciones activas"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Datos actuales obtenidos exitosamente",
            content = @Content(schema = @Schema(implementation = CalidadAireDTO.class))
        )
    })
    public ResponseEntity<List<CalidadAireDTO>> obtenerDatosActuales() {
        List<CalidadAireDTO> datos = calidadAireService.obtenerDatosActuales();
        return ResponseEntity.ok(datos);
    }

    /**
     * Obtiene datos optimizados para mapa (endpoint público).
     */
    @GetMapping("/mapa")
    @Operation(
        summary = "Obtener datos para visualización en mapa",
        description = "Devuelve datos optimizados para mostrar en mapas interactivos"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Datos para mapa obtenidos exitosamente",
            content = @Content(schema = @Schema(implementation = CalidadAireDTO.class))
        )
    })
    public ResponseEntity<List<CalidadAireDTO>> obtenerDatosParaMapa() {
        List<CalidadAireDTO> datos = calidadAireService.obtenerDatosParaMapa();
        return ResponseEntity.ok(datos);
    }

    /**
     * Obtiene datos históricos con paginación (requiere autenticación).
     */
    @GetMapping("/historico")
    @Operation(
        summary = "Obtener datos históricos",
        description = "Devuelve datos históricos de una estación específica con paginación",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Datos históricos obtenidos exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parámetros inválidos"
        )
    })
    @PreAuthorize("hasRole('CIUDADANO') or hasRole('AUTORIDAD') or hasRole('ADMIN')")
    public ResponseEntity<Page<CalidadAireDTO>> obtenerDatosHistoricos(
            @Parameter(description = "ID de la estación", required = true)
            @RequestParam Long estacionId,
            
            @Parameter(description = "Fecha de inicio (formato: yyyy-MM-dd'T'HH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            
            @Parameter(description = "Fecha de fin (formato: yyyy-MM-dd'T'HH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            
            Pageable pageable) {
        
        Page<CalidadAireDTO> datos = calidadAireService.obtenerDatosHistoricos(
                estacionId, fechaInicio, fechaFin, pageable);
        return ResponseEntity.ok(datos);
    }

    /**
     * Obtiene datos actuales de una estación específica.
     */
    @GetMapping("/estacion/{id}")
    @Operation(
        summary = "Obtener datos actuales de una estación",
        description = "Devuelve la medición más reciente de una estación específica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Datos obtenidos exitosamente",
            content = @Content(schema = @Schema(implementation = CalidadAireDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Estación no encontrada o sin datos"
        )
    })
    public ResponseEntity<CalidadAireDTO> obtenerDatosEstacion(
            @Parameter(description = "ID de la estación", required = true)
            @PathVariable Long id) {
        
        CalidadAireDTO datos = calidadAireService.obtenerDatosActualesEstacion(id);
        return ResponseEntity.ok(datos);
    }

    /**
     * Obtiene mediciones recientes (últimas N horas).
     */
    @GetMapping("/recientes")
    @Operation(
        summary = "Obtener mediciones recientes",
        description = "Devuelve mediciones de las últimas N horas",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Mediciones recientes obtenidas exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @PreAuthorize("hasRole('CIUDADANO') or hasRole('AUTORIDAD') or hasRole('ADMIN')")
    public ResponseEntity<List<CalidadAireDTO>> obtenerMedicionesRecientes(
            @Parameter(description = "Número de horas hacia atrás", example = "24")
            @RequestParam(defaultValue = "24") int horas) {
        
        List<CalidadAireDTO> datos = calidadAireService.obtenerMedicionesRecientes(horas);
        return ResponseEntity.ok(datos);
    }

    /**
     * Obtiene estadísticas por distrito.
     */
    @GetMapping("/estadisticas/distritos")
    @Operation(
        summary = "Obtener estadísticas por distrito",
        description = "Devuelve estadísticas de calidad del aire agrupadas por distrito",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Estadísticas obtenidas exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @PreAuthorize("hasRole('AUTORIDAD') or hasRole('ADMIN')")
    public ResponseEntity<List<CalidadAireService.EstadisticaDistritoDTO>> obtenerEstadisticasPorDistrito() {
        List<CalidadAireService.EstadisticaDistritoDTO> estadisticas = 
                calidadAireService.obtenerEstadisticasPorDistrito();
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Busca mediciones con AQI alto.
     */
    @GetMapping("/aqi-alto")
    @Operation(
        summary = "Buscar mediciones con AQI alto",
        description = "Devuelve mediciones que superan un umbral de AQI específico",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Mediciones con AQI alto obtenidas exitosamente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado"
        )
    })
    @PreAuthorize("hasRole('AUTORIDAD') or hasRole('ADMIN')")
    public ResponseEntity<List<CalidadAireDTO>> buscarMedicionesAqiAlto(
            @Parameter(description = "Umbral de AQI", example = "100")
            @RequestParam(defaultValue = "100") int umbralAqi,
            
            @Parameter(description = "Horas hacia atrás", example = "24")
            @RequestParam(defaultValue = "24") int horasAtras) {
        
        List<CalidadAireDTO> mediciones = calidadAireService.buscarMedicionesAqiAlto(umbralAqi, horasAtras);
        return ResponseEntity.ok(mediciones);
    }
}