package com.arequipa.aire.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Controlador REST para dashboard de autoridades.
 */
@RestController
@RequestMapping("/api/autoridades")
@Tag(name = "Dashboard Autoridades", description = "API para el dashboard de autoridades ambientales")
@CrossOrigin(origins = "*")
public class AutoridadesController {

    @Operation(summary = "Obtener resumen de cumplimiento", 
               description = "Devuelve un resumen del cumplimiento de estándares de calidad del aire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Datos obtenidos exitosamente")
    })
    @GetMapping("/cumplimiento")
    public ResponseEntity<ResumenCumplimientoDTO> getResumenCumplimiento() {
        
        ResumenCumplimientoDTO resumen = new ResumenCumplimientoDTO();
        
        // Generar datos simulados
        List<EstacionCumplimientoDTO> estaciones = generarDatosEstaciones();
        
        long estacionesCumpliendo = estaciones.stream().filter(EstacionCumplimientoDTO::isCompliant).count();
        int totalViolaciones = estaciones.stream().mapToInt(EstacionCumplimientoDTO::getViolations).sum();
        double aqiPromedio = estaciones.stream().mapToInt(EstacionCumplimientoDTO::getAqiAverage).average().orElse(0);
        
        resumen.setTotalEstaciones(estaciones.size());
        resumen.setEstacionesCumpliendo((int) estacionesCumpliendo);
        resumen.setTotalViolaciones(totalViolaciones);
        resumen.setAqiPromedio((int) aqiPromedio);
        resumen.setEstaciones(estaciones);
        resumen.setTimestamp(LocalDateTime.now().toString());
        
        return ResponseEntity.ok(resumen);
    }

    @Operation(summary = "Obtener estadísticas de calidad del aire", 
               description = "Devuelve estadísticas detalladas para gráficos del dashboard")
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasDTO> getEstadisticas() {
        
        EstadisticasDTO estadisticas = new EstadisticasDTO();
        
        // Datos para gráfico de barras - cumplimiento por estación
        List<DatoGraficoDTO> datosBarras = new ArrayList<>();
        String[] ubicaciones = {"Centro Histórico", "Cercado de Arequipa", "Yanahuara", "Cayma", "José Luis Bustamante y Rivero"};
        Random random = new Random();
        
        for (String ubicacion : ubicaciones) {
            DatoGraficoDTO dato = new DatoGraficoDTO();
            dato.setName(ubicacion);
            dato.setValue(50 + random.nextInt(101)); // AQI entre 50-150
            datosBarras.add(dato);
        }
        estadisticas.setDatosBarras(datosBarras);
        
        // Datos para gráfico circular - cumplimiento general
        List<DatoCircularDTO> datosCirculares = Arrays.asList(
            new DatoCircularDTO("Cumple ECA-Aire", 3, "#4CAF50"),
            new DatoCircularDTO("No Cumple", 2, "#F44336")
        );
        estadisticas.setDatosCirculares(datosCirculares);
        
        // Tendencias de los últimos 30 días
        List<DatoTendenciaDTO> tendencias = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            DatoTendenciaDTO tendencia = new DatoTendenciaDTO();
            tendencia.setDate(LocalDate.now().minusDays(i).toString());
            tendencia.setAqi(60 + random.nextInt(80)); // AQI entre 60-140
            tendencia.setViolations(random.nextInt(5));
            tendencias.add(tendencia);
        }
        estadisticas.setTendencias(tendencias);
        
        return ResponseEntity.ok(estadisticas);
    }

    @Operation(summary = "Generar reporte de calidad del aire", 
               description = "Genera un reporte detallado de la calidad del aire")
    @GetMapping("/reporte")
    public ResponseEntity<ReporteDTO> generarReporte(
            @Parameter(description = "Fecha de inicio del reporte")
            @RequestParam(required = false) String fechaInicio,
            @Parameter(description = "Fecha de fin del reporte")
            @RequestParam(required = false) String fechaFin) {
        
        ReporteDTO reporte = new ReporteDTO();
        reporte.setFechaGeneracion(LocalDateTime.now().toString());
        reporte.setFechaInicio(fechaInicio != null ? fechaInicio : LocalDate.now().minusDays(30).toString());
        reporte.setFechaFin(fechaFin != null ? fechaFin : LocalDate.now().toString());
        
        // Generar datos del reporte
        List<EstacionCumplimientoDTO> estaciones = generarDatosEstaciones();
        
        int estacionesCumpliendo = (int) estaciones.stream().filter(EstacionCumplimientoDTO::isCompliant).count();
        int totalViolaciones = estaciones.stream().mapToInt(EstacionCumplimientoDTO::getViolations).sum();
        double aqiPromedio = estaciones.stream().mapToInt(EstacionCumplimientoDTO::getAqiAverage).average().orElse(0);
        
        reporte.setTotalEstaciones(estaciones.size());
        reporte.setEstacionesCumpliendo(estacionesCumpliendo);
        reporte.setTotalViolaciones(totalViolaciones);
        reporte.setAqiPromedio((int) aqiPromedio);
        reporte.setDetalleEstaciones(estaciones);
        
        // Generar resumen ejecutivo
        String resumenEjecutivo = String.format(
            "Durante el período analizado, %d de %d estaciones cumplieron con los estándares ECA-Aire. " +
            "Se registraron %d violaciones en total con un AQI promedio de %d.",
            estacionesCumpliendo, estaciones.size(), totalViolaciones, (int) aqiPromedio
        );
        reporte.setResumenEjecutivo(resumenEjecutivo);
        
        return ResponseEntity.ok(reporte);
    }

    @Operation(summary = "Obtener alertas de emergencia", 
               description = "Devuelve alertas de emergencia activas")
    @GetMapping("/alertas-emergencia")
    public ResponseEntity<List<AlertaEmergenciaDTO>> getAlertasEmergencia() {
        
        List<AlertaEmergenciaDTO> alertas = new ArrayList<>();
        Random random = new Random();
        
        // Generar algunas alertas de emergencia simuladas
        String[] ubicaciones = {"Centro Histórico", "José Luis Bustamante y Rivero"};
        
        for (String ubicacion : ubicaciones) {
            if (random.nextBoolean()) { // 50% de probabilidad de tener alerta
                AlertaEmergenciaDTO alerta = new AlertaEmergenciaDTO();
                alerta.setUbicacion(ubicacion);
                alerta.setAqi(151 + random.nextInt(100)); // AQI entre 151-250
                alerta.setNivel("EMERGENCIA");
                alerta.setDescripcion("Calidad del aire peligrosa detectada");
                alerta.setTimestamp(LocalDateTime.now().toString());
                alertas.add(alerta);
            }
        }
        
        return ResponseEntity.ok(alertas);
    }

    private List<EstacionCumplimientoDTO> generarDatosEstaciones() {
        List<EstacionCumplimientoDTO> estaciones = new ArrayList<>();
        String[] ubicaciones = {"Centro Histórico", "Cercado de Arequipa", "Yanahuara", "Cayma", "José Luis Bustamante y Rivero"};
        Random random = new Random();
        
        for (String ubicacion : ubicaciones) {
            EstacionCumplimientoDTO estacion = new EstacionCumplimientoDTO();
            estacion.setLocation(ubicacion);
            
            int aqi = 50 + random.nextInt(101); // AQI entre 50-150
            estacion.setAqiAverage(aqi);
            estacion.setCompliant(aqi <= 100);
            estacion.setViolations(aqi > 100 ? random.nextInt(5) + 1 : 0);
            
            estaciones.add(estacion);
        }
        
        return estaciones;
    }

    // DTOs
    public static class ResumenCumplimientoDTO {
        private Integer totalEstaciones;
        private Integer estacionesCumpliendo;
        private Integer totalViolaciones;
        private Integer aqiPromedio;
        private List<EstacionCumplimientoDTO> estaciones;
        private String timestamp;

        // Getters y setters
        public Integer getTotalEstaciones() { return totalEstaciones; }
        public void setTotalEstaciones(Integer totalEstaciones) { this.totalEstaciones = totalEstaciones; }
        public Integer getEstacionesCumpliendo() { return estacionesCumpliendo; }
        public void setEstacionesCumpliendo(Integer estacionesCumpliendo) { this.estacionesCumpliendo = estacionesCumpliendo; }
        public Integer getTotalViolaciones() { return totalViolaciones; }
        public void setTotalViolaciones(Integer totalViolaciones) { this.totalViolaciones = totalViolaciones; }
        public Integer getAqiPromedio() { return aqiPromedio; }
        public void setAqiPromedio(Integer aqiPromedio) { this.aqiPromedio = aqiPromedio; }
        public List<EstacionCumplimientoDTO> getEstaciones() { return estaciones; }
        public void setEstaciones(List<EstacionCumplimientoDTO> estaciones) { this.estaciones = estaciones; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }

    public static class EstacionCumplimientoDTO {
        private String location;
        private boolean compliant;
        private Integer aqiAverage;
        private Integer violations;

        // Getters y setters
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public boolean isCompliant() { return compliant; }
        public void setCompliant(boolean compliant) { this.compliant = compliant; }
        public Integer getAqiAverage() { return aqiAverage; }
        public void setAqiAverage(Integer aqiAverage) { this.aqiAverage = aqiAverage; }
        public Integer getViolations() { return violations; }
        public void setViolations(Integer violations) { this.violations = violations; }
    }

    public static class EstadisticasDTO {
        private List<DatoGraficoDTO> datosBarras;
        private List<DatoCircularDTO> datosCirculares;
        private List<DatoTendenciaDTO> tendencias;

        // Getters y setters
        public List<DatoGraficoDTO> getDatosBarras() { return datosBarras; }
        public void setDatosBarras(List<DatoGraficoDTO> datosBarras) { this.datosBarras = datosBarras; }
        public List<DatoCircularDTO> getDatosCirculares() { return datosCirculares; }
        public void setDatosCirculares(List<DatoCircularDTO> datosCirculares) { this.datosCirculares = datosCirculares; }
        public List<DatoTendenciaDTO> getTendencias() { return tendencias; }
        public void setTendencias(List<DatoTendenciaDTO> tendencias) { this.tendencias = tendencias; }
    }

    public static class DatoGraficoDTO {
        private String name;
        private Integer value;

        // Getters y setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getValue() { return value; }
        public void setValue(Integer value) { this.value = value; }
    }

    public static class DatoCircularDTO {
        private String name;
        private Integer value;
        private String color;

        public DatoCircularDTO(String name, Integer value, String color) {
            this.name = name;
            this.value = value;
            this.color = color;
        }

        // Getters y setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getValue() { return value; }
        public void setValue(Integer value) { this.value = value; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }

    public static class DatoTendenciaDTO {
        private String date;
        private Integer aqi;
        private Integer violations;

        // Getters y setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public Integer getAqi() { return aqi; }
        public void setAqi(Integer aqi) { this.aqi = aqi; }
        public Integer getViolations() { return violations; }
        public void setViolations(Integer violations) { this.violations = violations; }
    }

    public static class ReporteDTO {
        private String fechaGeneracion;
        private String fechaInicio;
        private String fechaFin;
        private Integer totalEstaciones;
        private Integer estacionesCumpliendo;
        private Integer totalViolaciones;
        private Integer aqiPromedio;
        private String resumenEjecutivo;
        private List<EstacionCumplimientoDTO> detalleEstaciones;

        // Getters y setters
        public String getFechaGeneracion() { return fechaGeneracion; }
        public void setFechaGeneracion(String fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
        public String getFechaInicio() { return fechaInicio; }
        public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
        public String getFechaFin() { return fechaFin; }
        public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
        public Integer getTotalEstaciones() { return totalEstaciones; }
        public void setTotalEstaciones(Integer totalEstaciones) { this.totalEstaciones = totalEstaciones; }
        public Integer getEstacionesCumpliendo() { return estacionesCumpliendo; }
        public void setEstacionesCumpliendo(Integer estacionesCumpliendo) { this.estacionesCumpliendo = estacionesCumpliendo; }
        public Integer getTotalViolaciones() { return totalViolaciones; }
        public void setTotalViolaciones(Integer totalViolaciones) { this.totalViolaciones = totalViolaciones; }
        public Integer getAqiPromedio() { return aqiPromedio; }
        public void setAqiPromedio(Integer aqiPromedio) { this.aqiPromedio = aqiPromedio; }
        public String getResumenEjecutivo() { return resumenEjecutivo; }
        public void setResumenEjecutivo(String resumenEjecutivo) { this.resumenEjecutivo = resumenEjecutivo; }
        public List<EstacionCumplimientoDTO> getDetalleEstaciones() { return detalleEstaciones; }
        public void setDetalleEstaciones(List<EstacionCumplimientoDTO> detalleEstaciones) { this.detalleEstaciones = detalleEstaciones; }
    }

    public static class AlertaEmergenciaDTO {
        private String ubicacion;
        private Integer aqi;
        private String nivel;
        private String descripcion;
        private String timestamp;

        // Getters y setters
        public String getUbicacion() { return ubicacion; }
        public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
        public Integer getAqi() { return aqi; }
        public void setAqi(Integer aqi) { this.aqi = aqi; }
        public String getNivel() { return nivel; }
        public void setNivel(String nivel) { this.nivel = nivel; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
}
