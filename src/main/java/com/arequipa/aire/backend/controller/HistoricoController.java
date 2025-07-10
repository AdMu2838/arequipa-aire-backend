package com.arequipa.aire.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Controlador REST para datos históricos y predicciones.
 */
@RestController
@RequestMapping("/api/historicos")
@Tag(name = "Datos Históricos", description = "API para obtener datos históricos y predicciones")
@CrossOrigin(origins = "*")
public class HistoricoController {

    @Operation(summary = "Obtener datos históricos", 
               description = "Devuelve datos históricos de calidad del aire para una ubicación y período específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Datos obtenidos exitosamente")
    })
    @GetMapping("/ubicacion/{ubicacionId}")
    public ResponseEntity<List<DatoHistoricoDTO>> getDatosHistoricos(
            @Parameter(description = "ID de la ubicación")
            @PathVariable String ubicacionId,
            @Parameter(description = "Período de tiempo (7days, 30days, 180days)")
            @RequestParam(defaultValue = "7days") String timeRange) {
        
        try {
            List<DatoHistoricoDTO> datos = generarDatosHistoricos(ubicacionId, timeRange);
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener comparación de contaminantes", 
               description = "Devuelve datos de comparación de contaminantes para gráficos")
    @GetMapping("/comparacion/{ubicacionId}")
    public ResponseEntity<List<DatoComparacionDTO>> getComparacionContaminantes(
            @Parameter(description = "ID de la ubicación")
            @PathVariable String ubicacionId) {
        
        List<DatoComparacionDTO> datos = generarDatosComparacion(ubicacionId);
        return ResponseEntity.ok(datos);
    }

    private List<DatoHistoricoDTO> generarDatosHistoricos(String ubicacionId, String timeRange) {
        int dias = switch (timeRange) {
            case "7days" -> 7;
            case "30days" -> 30;
            case "180days" -> 180;
            default -> 7;
        };

        List<DatoHistoricoDTO> historicos = new ArrayList<>();
        Random random = new Random();

        // Valores base por ubicación
        Map<String, Integer> valoresBase = Map.of(
            "centro", 75,
            "cercado", 65,
            "yanahuara", 55,
            "cayma", 45,
            "bustamante", 85
        );

        int baseAQI = valoresBase.getOrDefault(ubicacionId, 70);

        // Generar datos históricos + 2 días de predicción
        for (int i = dias; i >= -2; i--) {
            LocalDateTime fecha = LocalDateTime.now().minusDays(i);
            
            // Agregar algo de variación estacional y aleatoria
            double variacion = Math.sin(i * 0.1) * 20 + (random.nextDouble() - 0.5) * 30;
            int aqi = Math.max(0, (int) (baseAQI + variacion));
            
            DatoHistoricoDTO dato = new DatoHistoricoDTO();
            dato.setDate(fecha.toLocalDate().toString());
            dato.setAqi(aqi);
            dato.setPm25((int) (aqi * 0.4 + (random.nextDouble() - 0.5) * 10));
            dato.setPm10((int) (aqi * 0.8 + (random.nextDouble() - 0.5) * 15));
            dato.setNo2((int) (aqi * 0.5 + (random.nextDouble() - 0.5) * 8));
            dato.setO3((int) (aqi * 1.2 + (random.nextDouble() - 0.5) * 20));
            dato.setCo((int) (aqi * 25 + (random.nextDouble() - 0.5) * 500));
            dato.setPredicted(i < 0); // Los últimos 2 días son predicciones
            
            historicos.add(dato);
        }

        return historicos;
    }

    private List<DatoComparacionDTO> generarDatosComparacion(String ubicacionId) {
        List<DatoComparacionDTO> datos = new ArrayList<>();
        String[] contaminantes = {"PM2.5", "PM10", "NO₂", "O₃", "CO"};
        int[] limites = {25, 50, 40, 100, 10000};
        
        Random random = new Random();
        
        for (int i = 0; i < contaminantes.length; i++) {
            DatoComparacionDTO dato = new DatoComparacionDTO();
            dato.setName(contaminantes[i]);
            dato.setValue(random.nextInt(limites[i]) + (int)(limites[i] * 0.1));
            dato.setLimit(limites[i]);
            datos.add(dato);
        }
        
        return datos;
    }

    // DTOs internos
    public static class DatoHistoricoDTO {
        private String date;
        private Integer aqi;
        private Integer pm25;
        private Integer pm10;
        private Integer no2;
        private Integer o3;
        private Integer co;
        private Boolean predicted;

        // Getters y setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public Integer getAqi() { return aqi; }
        public void setAqi(Integer aqi) { this.aqi = aqi; }
        public Integer getPm25() { return pm25; }
        public void setPm25(Integer pm25) { this.pm25 = pm25; }
        public Integer getPm10() { return pm10; }
        public void setPm10(Integer pm10) { this.pm10 = pm10; }
        public Integer getNo2() { return no2; }
        public void setNo2(Integer no2) { this.no2 = no2; }
        public Integer getO3() { return o3; }
        public void setO3(Integer o3) { this.o3 = o3; }
        public Integer getCo() { return co; }
        public void setCo(Integer co) { this.co = co; }
        public Boolean getPredicted() { return predicted; }
        public void setPredicted(Boolean predicted) { this.predicted = predicted; }
    }

    public static class DatoComparacionDTO {
        private String name;
        private Integer value;
        private Integer limit;

        // Getters y setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getValue() { return value; }
        public void setValue(Integer value) { this.value = value; }
        public Integer getLimit() { return limit; }
        public void setLimit(Integer limit) { this.limit = limit; }
    }
}
