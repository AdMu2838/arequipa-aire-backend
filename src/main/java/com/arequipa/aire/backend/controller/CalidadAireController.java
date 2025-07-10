package com.arequipa.aire.backend.controller;

import com.arequipa.aire.backend.entity.Medicion;
import com.arequipa.aire.backend.repository.MedicionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Obtener calidad del aire actual", 
               description = "Devuelve los datos actuales de calidad del aire de todas las estaciones")
    @GetMapping("/actual")
    public ResponseEntity<List<Object>> getCalidadAireActual() {
        try {
            // Obtener las mediciones más recientes
            List<Medicion> medicionesRecientes = medicionRepository.findAll()
                    .stream()
                    .sorted((m1, m2) -> m2.getFechaMedicion().compareTo(m1.getFechaMedicion()))
                    .limit(10)
                    .toList();
            
            List<Object> datos = new ArrayList<>();
            for (Medicion medicion : medicionesRecientes) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", medicion.getId());
                item.put("estacionId", medicion.getEstacion().getId());
                item.put("estacion", Map.of(
                    "id", medicion.getEstacion().getId(),
                    "nombre", medicion.getEstacion().getNombre(),
                    "latitud", medicion.getEstacion().getLatitud(),
                    "longitud", medicion.getEstacion().getLongitud()
                ));
                item.put("fechaHora", medicion.getFechaMedicion().toString());
                item.put("pm25", medicion.getPm25());
                item.put("pm10", medicion.getPm10());
                item.put("no2", medicion.getNo2());
                item.put("o3", medicion.getO3());
                item.put("co", medicion.getCo());
                item.put("so2", medicion.getSo2());
                
                // Calcular AQI simple basado en PM2.5
                Integer aqi = calcularAQI(medicion.getPm25());
                item.put("aqi", aqi);
                item.put("categoria", obtenerCategoriaAQI(aqi));
                
                datos.add(item);
            }
            
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @Operation(summary = "Obtener calidad del aire por estación", 
               description = "Devuelve los datos de calidad del aire para una estación específica")
    @GetMapping("/estacion/{estacionId}")
    public ResponseEntity<Object> getCalidadAireByEstacion(@PathVariable Long estacionId) {
        try {
            // Buscar la medición más reciente de la estación
            Optional<Medicion> medicionReciente = medicionRepository.findAll()
                    .stream()
                    .filter(m -> m.getEstacion().getId().equals(estacionId))
                    .max(Comparator.comparing(Medicion::getFechaMedicion));
            
            if (medicionReciente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Medicion medicion = medicionReciente.get();
            Map<String, Object> item = new HashMap<>();
            item.put("id", medicion.getId());
            item.put("estacionId", medicion.getEstacion().getId());
            item.put("estacion", Map.of(
                "id", medicion.getEstacion().getId(),
                "nombre", medicion.getEstacion().getNombre(),
                "latitud", medicion.getEstacion().getLatitud(),
                "longitud", medicion.getEstacion().getLongitud()
            ));
            item.put("fechaHora", medicion.getFechaMedicion().toString());
            item.put("pm25", medicion.getPm25());
            item.put("pm10", medicion.getPm10());
            item.put("no2", medicion.getNo2());
            item.put("o3", medicion.getO3());
            item.put("co", medicion.getCo());
            item.put("so2", medicion.getSo2());
            
            // Calcular AQI simple basado en PM2.5
            Integer aqi = calcularAQI(medicion.getPm25());
            item.put("aqi", aqi);
            item.put("categoria", obtenerCategoriaAQI(aqi));
            
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener índice de calidad del aire por estación", 
               description = "Devuelve el índice de calidad del aire para una estación específica")
    @GetMapping("/indice/{estacionId}")
    public ResponseEntity<Object> getIndiceCalidadAire(@PathVariable Long estacionId) {
        try {
            // Buscar la medición más reciente de la estación
            Optional<Medicion> medicionReciente = medicionRepository.findAll()
                    .stream()
                    .filter(m -> m.getEstacion().getId().equals(estacionId))
                    .max(Comparator.comparing(Medicion::getFechaMedicion));
            
            if (medicionReciente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Medicion medicion = medicionReciente.get();
            Integer aqi = calcularAQI(medicion.getPm25());
            
            Map<String, Object> indice = new HashMap<>();
            indice.put("aqi", aqi);
            indice.put("categoria", obtenerCategoriaAQI(aqi));
            indice.put("fechaHora", medicion.getFechaMedicion().toString());
            indice.put("estacionId", estacionId);
            
            return ResponseEntity.ok(indice);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
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
