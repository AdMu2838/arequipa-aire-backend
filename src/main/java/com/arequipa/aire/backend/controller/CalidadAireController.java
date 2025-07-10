package com.arequipa.aire.backend.controller;

import com.arequipa.aire.backend.dto.CalidadAireFrontendDTO;
import com.arequipa.aire.backend.entity.Medicion;
import com.arequipa.aire.backend.repository.MedicionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Controlador REST para datos de calidad del aire.
 */
@RestController
@RequestMapping("/api/calidad-aire")
@Tag(name = "Calidad del Aire", description = "API para obtener datos de calidad del aire")
@CrossOrigin(origins = "*")
public class CalidadAireController {

    @Autowired
    private MedicionRepository medicionRepository;

    @Operation(summary = "Obtener calidad del aire por ubicación", 
               description = "Devuelve los datos actuales de calidad del aire para una ubicación específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Datos obtenidos exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ubicación no encontrada")
    })
    @GetMapping("/ubicacion/{ubicacionId}")
    public ResponseEntity<CalidadAireFrontendDTO> getCalidadAirePorUbicacion(
            @Parameter(description = "ID de la ubicación (centro, cercado, yanahuara, cayma, bustamante)")
            @PathVariable String ubicacionId) {
        
        try {
            // Mapear ubicaciones del frontend a nombres de estaciones
            String nombreEstacion = mapearUbicacion(ubicacionId);
            
            // Buscar la estación más reciente para esa ubicación
            Optional<Medicion> medicionReciente = medicionRepository
                    .findTopByEstacionNombreContainingIgnoreCaseOrderByFechaMedicionDesc(nombreEstacion);
            
            if (medicionReciente.isEmpty()) {
                // Si no hay datos, generar datos simulados para desarrollo
                return ResponseEntity.ok(generarDatosSimulados(ubicacionId, nombreEstacion));
            }
            
            CalidadAireFrontendDTO dto = convertirACalidadAireDTO(medicionReciente.get());
            return ResponseEntity.ok(dto);
            
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener datos de todas las estaciones para el mapa", 
               description = "Devuelve los datos actuales de todas las estaciones para mostrar en el mapa")
    @GetMapping("/mapa")
    public ResponseEntity<Map<String, CalidadAireFrontendDTO>> getDatosParaMapa() {
        Map<String, CalidadAireFrontendDTO> datosMapa = new HashMap<>();
        
        // Ubicaciones del frontend
        String[] ubicaciones = {"centro", "cercado", "yanahuara", "cayma", "bustamante"};
        
        for (String ubicacion : ubicaciones) {
            String nombreEstacion = mapearUbicacion(ubicacion);
            Optional<Medicion> medicionReciente = medicionRepository
                    .findTopByEstacionNombreContainingIgnoreCaseOrderByFechaMedicionDesc(nombreEstacion);
            
            CalidadAireFrontendDTO dto;
            if (medicionReciente.isPresent()) {
                dto = convertirACalidadAireDTO(medicionReciente.get());
            } else {
                dto = generarDatosSimulados(ubicacion, nombreEstacion);
            }
            
            datosMapa.put(ubicacion, dto);
        }
        
        return ResponseEntity.ok(datosMapa);
    }

    @Operation(summary = "Obtener recomendaciones de salud", 
               description = "Devuelve recomendaciones de salud basadas en el AQI")
    @GetMapping("/recomendaciones/{aqi}")
    public ResponseEntity<List<String>> getRecomendacionesSalud(
            @Parameter(description = "Valor del AQI")
            @PathVariable int aqi) {
        
        List<String> recomendaciones = generarRecomendacionesSalud(aqi);
        return ResponseEntity.ok(recomendaciones);
    }

    private String mapearUbicacion(String ubicacionId) {
        Map<String, String> mapeo = Map.of(
            "centro", "Centro Histórico",
            "cercado", "Cercado de Arequipa",
            "yanahuara", "Yanahuara", 
            "cayma", "Cayma",
            "bustamante", "José Luis Bustamante y Rivero"
        );
        return mapeo.getOrDefault(ubicacionId, "Centro Histórico");
    }

    private CalidadAireFrontendDTO convertirACalidadAireDTO(Medicion medicion) {
        CalidadAireFrontendDTO dto = new CalidadAireFrontendDTO();
        dto.setLocation(medicion.getEstacion().getNombre());
        dto.setTimestamp(medicion.getFechaMedicion().toString());
        
        // Calcular AQI basado en PM2.5 (simplificado)
        int aqi = calcularAQI(medicion.getPm25());
        dto.setAqi(aqi);
        dto.setCategory(getCategoriaAQI(aqi));
        
        // Establecer contaminantes
        Map<String, Map<String, Object>> pollutants = new HashMap<>();
        pollutants.put("pm25", Map.of("value", medicion.getPm25(), "unit", "µg/m³"));
        pollutants.put("pm10", Map.of("value", medicion.getPm10(), "unit", "µg/m³"));
        pollutants.put("no2", Map.of("value", medicion.getNo2(), "unit", "µg/m³"));
        pollutants.put("o3", Map.of("value", medicion.getO3(), "unit", "µg/m³"));
        pollutants.put("co", Map.of("value", medicion.getCo(), "unit", "µg/m³"));
        dto.setPollutants(pollutants);
        
        dto.setHealthRecommendations(generarRecomendacionesSalud(aqi));
        
        return dto;
    }

    private CalidadAireFrontendDTO generarDatosSimulados(String ubicacionId, String nombreEstacion) {
        CalidadAireFrontendDTO dto = new CalidadAireFrontendDTO();
        dto.setLocation(nombreEstacion);
        dto.setTimestamp(LocalDateTime.now().toString());
        
        // Valores base por ubicación para simular datos realistas
        Map<String, Integer> valoresBase = Map.of(
            "centro", 75,
            "cercado", 65,
            "yanahuara", 55,
            "cayma", 45,
            "bustamante", 85
        );
        
        int baseAQI = valoresBase.getOrDefault(ubicacionId, 70);
        // Agregar variación aleatoria
        Random random = new Random();
        int aqi = Math.max(0, baseAQI + random.nextInt(41) - 20); // ±20
        
        dto.setAqi(aqi);
        dto.setCategory(getCategoriaAQI(aqi));
        
        // Generar valores de contaminantes basados en AQI
        Map<String, Map<String, Object>> pollutants = new HashMap<>();
        pollutants.put("pm25", Map.of("value", Math.max(0, aqi * 0.4 + random.nextInt(21) - 10), "unit", "µg/m³"));
        pollutants.put("pm10", Map.of("value", Math.max(0, aqi * 0.8 + random.nextInt(31) - 15), "unit", "µg/m³"));
        pollutants.put("no2", Map.of("value", Math.max(0, aqi * 0.5 + random.nextInt(17) - 8), "unit", "µg/m³"));
        pollutants.put("o3", Map.of("value", Math.max(0, aqi * 1.2 + random.nextInt(41) - 20), "unit", "µg/m³"));
        pollutants.put("co", Map.of("value", Math.max(0, aqi * 25 + random.nextInt(1001) - 500), "unit", "µg/m³"));
        dto.setPollutants(pollutants);
        
        dto.setHealthRecommendations(generarRecomendacionesSalud(aqi));
        
        return dto;
    }

    private int calcularAQI(double pm25) {
        // Cálculo simplificado del AQI basado en PM2.5
        if (pm25 <= 12.0) return (int) (pm25 * 50 / 12.0);
        if (pm25 <= 35.4) return (int) (50 + (pm25 - 12.0) * 50 / 23.4);
        if (pm25 <= 55.4) return (int) (100 + (pm25 - 35.4) * 50 / 20.0);
        if (pm25 <= 150.4) return (int) (150 + (pm25 - 55.4) * 50 / 95.0);
        if (pm25 <= 250.4) return (int) (200 + (pm25 - 150.4) * 100 / 100.0);
        return (int) (300 + (pm25 - 250.4) * 100 / 149.6);
    }

    private String getCategoriaAQI(int aqi) {
        if (aqi <= 50) return "Bueno";
        if (aqi <= 100) return "Moderado";
        if (aqi <= 150) return "Dañino para grupos sensibles";
        if (aqi <= 200) return "Dañino";
        if (aqi <= 300) return "Muy dañino";
        return "Peligroso";
    }

    private List<String> generarRecomendacionesSalud(int aqi) {
        if (aqi <= 50) {
            return List.of(
                "La calidad del aire es buena, disfruta de actividades al aire libre",
                "Ideal para ejercitarse al aire libre",
                "No se necesitan precauciones especiales"
            );
        }
        if (aqi <= 100) {
            return List.of(
                "Calidad del aire aceptable para la mayoría de personas",
                "Personas sensibles pueden experimentar síntomas menores",
                "Considera reducir tiempo de actividades intensas si eres sensible"
            );
        }
        if (aqi <= 150) {
            return List.of(
                "Personas sensibles deben limitar actividades prolongadas al aire libre",
                "Usar mascarilla si tienes condiciones respiratorias",
                "Evita ejercicio intenso al aire libre",
                "Mantén ventanas cerradas durante las horas pico"
            );
        }
        if (aqi <= 200) {
            return List.of(
                "Todos deben limitar actividades prolongadas al aire libre",
                "Usar mascarilla cuando salgas",
                "Evita completamente ejercicio al aire libre",
                "Considera usar purificadores de aire en casa"
            );
        }
        return List.of(
            "Evita salir al aire libre",
            "Usa mascarilla N95 si debes salir",
            "Mantén todas las ventanas cerradas",
            "Considera evacuar si tienes condiciones respiratorias graves"
        );
    }
}
