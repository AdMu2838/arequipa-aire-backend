package com.arequipa.aire.backend.controller;

import com.arequipa.aire.backend.entity.Medicion;
import com.arequipa.aire.backend.repository.MedicionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador REST para datos históricos.
 */
@RestController
@RequestMapping("/api/historico")
@Tag(name = "Datos Históricos", description = "API para obtener datos históricos")
@CrossOrigin(origins = "*")
public class HistoricoController {

    @Autowired
    private MedicionRepository medicionRepository;

    @Operation(summary = "Obtener datos históricos por estación", 
               description = "Devuelve datos históricos de calidad del aire para una estación específica")
    @GetMapping("/estacion/{estacionId}")
    public ResponseEntity<List<Object>> getHistorico(
            @Parameter(description = "ID de la estación")
            @PathVariable Long estacionId,
            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)")
            @RequestParam String fechaInicio,
            @Parameter(description = "Fecha de fin (YYYY-MM-DD)")
            @RequestParam String fechaFin) {
        
        try {
            LocalDateTime inicio = LocalDateTime.parse(fechaInicio + "T00:00:00");
            LocalDateTime fin = LocalDateTime.parse(fechaFin + "T23:59:59");
            
            // Filtrar mediciones por estación y rango de fechas
            List<Medicion> mediciones = medicionRepository.findAll()
                    .stream()
                    .filter(m -> m.getEstacion().getId().equals(estacionId))
                    .filter(m -> m.getFechaMedicion().isAfter(inicio) && m.getFechaMedicion().isBefore(fin))
                    .sorted(Comparator.comparing(Medicion::getFechaMedicion))
                    .collect(Collectors.toList());
            
            List<Object> datos = new ArrayList<>();
            for (Medicion medicion : mediciones) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", medicion.getId());
                item.put("fechaHora", medicion.getFechaMedicion().toString());
                item.put("pm25", medicion.getPm25());
                item.put("pm10", medicion.getPm10());
                item.put("no2", medicion.getNo2());
                item.put("o3", medicion.getO3());
                item.put("co", medicion.getCo());
                item.put("so2", medicion.getSo2());
                item.put("temperatura", medicion.getTemperatura());
                item.put("humedad", medicion.getHumedad());
                
                // Calcular AQI simple basado en PM2.5
                Integer aqi = calcularAQI(medicion.getPm25());
                item.put("aqi", aqi);
                item.put("categoria", obtenerCategoriaAQI(aqi));
                
                datos.add(item);
            }
            
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener promedio histórico", 
               description = "Devuelve promedios históricos por período")
    @GetMapping("/promedio/{estacionId}")
    public ResponseEntity<Object> getPromedioHistorico(
            @Parameter(description = "ID de la estación")
            @PathVariable Long estacionId,
            @Parameter(description = "Período (dia, semana, mes)")
            @RequestParam String periodo) {
        
        try {
            // Obtener datos de los últimos 30 días para calcular promedios
            LocalDateTime fechaInicio = LocalDateTime.now().minusDays(30);
            LocalDateTime fechaFin = LocalDateTime.now();
            
            List<Medicion> mediciones = medicionRepository.findAll()
                    .stream()
                    .filter(m -> m.getEstacion().getId().equals(estacionId))
                    .filter(m -> m.getFechaMedicion().isAfter(fechaInicio) && m.getFechaMedicion().isBefore(fechaFin))
                    .collect(Collectors.toList());
            
            if (mediciones.isEmpty()) {
                return ResponseEntity.ok(Map.of("promedio", 0, "periodo", periodo));
            }
            
            // Calcular promedios
            double promedioPm25 = mediciones.stream()
                    .mapToDouble(m -> m.getPm25() != null ? m.getPm25() : 0)
                    .average().orElse(0);
            double promedioPm10 = mediciones.stream()
                    .mapToDouble(m -> m.getPm10() != null ? m.getPm10() : 0)
                    .average().orElse(0);
            double promedioAQI = mediciones.stream()
                    .mapToDouble(m -> {
                        Integer aqi = calcularAQI(m.getPm25());
                        return aqi != null ? aqi : 0;
                    })
                    .average().orElse(0);
            
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("periodo", periodo);
            resultado.put("pm25", promedioPm25);
            resultado.put("pm10", promedioPm10);
            resultado.put("aqi", promedioAQI);
            resultado.put("totalMediciones", mediciones.size());
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener comparación histórica", 
               description = "Devuelve datos de comparación histórica")
    @GetMapping("/comparacion/{estacionId}")
    public ResponseEntity<Object> getComparacionHistorica(
            @Parameter(description = "ID de la estación")
            @PathVariable Long estacionId,
            @Parameter(description = "Fecha de inicio")
            @RequestParam String fechaInicio,
            @Parameter(description = "Fecha de fin")
            @RequestParam String fechaFin) {
        
        try {
            LocalDateTime inicio = LocalDateTime.parse(fechaInicio + "T00:00:00");
            LocalDateTime fin = LocalDateTime.parse(fechaFin + "T23:59:59");
            
            List<Medicion> mediciones = medicionRepository.findAll()
                    .stream()
                    .filter(m -> m.getEstacion().getId().equals(estacionId))
                    .filter(m -> m.getFechaMedicion().isAfter(inicio) && m.getFechaMedicion().isBefore(fin))
                    .collect(Collectors.toList());
            
            Map<String, Object> comparacion = new HashMap<>();
            comparacion.put("totalMediciones", mediciones.size());
            comparacion.put("fechaInicio", fechaInicio);
            comparacion.put("fechaFin", fechaFin);
            comparacion.put("estacionId", estacionId);
            
            if (!mediciones.isEmpty()) {
                List<Integer> aqiValues = mediciones.stream()
                        .map(m -> calcularAQI(m.getPm25()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                
                if (!aqiValues.isEmpty()) {
                    comparacion.put("maxAQI", aqiValues.stream().mapToInt(Integer::intValue).max().orElse(0));
                    comparacion.put("minAQI", aqiValues.stream().mapToInt(Integer::intValue).min().orElse(0));
                    comparacion.put("promedioAQI", aqiValues.stream().mapToDouble(Integer::doubleValue).average().orElse(0));
                }
            }
            
            return ResponseEntity.ok(comparacion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Calcula el AQI basado en PM2.5 (simplificado)
     */
    private Integer calcularAQI(Double pm25) {
        if (pm25 == null) return null;
        
        if (pm25 <= 12.0) return (int) (pm25 * 50 / 12.0);
        if (pm25 <= 35.4) return (int) (50 + (pm25 - 12.0) * 50 / 23.4);
        if (pm25 <= 55.4) return (int) (100 + (pm25 - 35.4) * 50 / 20.0);
        if (pm25 <= 150.4) return (int) (150 + (pm25 - 55.4) * 50 / 95.0);
        if (pm25 <= 250.4) return (int) (200 + (pm25 - 150.4) * 100 / 100.0);
        return (int) (300 + (pm25 - 250.4) * 100 / 149.6);
    }
    
    /**
     * Obtiene la categoría del AQI
     */
    private String obtenerCategoriaAQI(Integer aqi) {
        if (aqi == null) return "Sin datos";
        if (aqi <= 50) return "Bueno";
        if (aqi <= 100) return "Moderado";
        if (aqi <= 150) return "Dañino para grupos sensibles";
        if (aqi <= 200) return "Dañino";
        if (aqi <= 300) return "Muy dañino";
        return "Peligroso";
    }
}
