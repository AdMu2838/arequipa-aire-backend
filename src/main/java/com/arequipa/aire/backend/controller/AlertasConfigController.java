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
 * Controlador REST para configuración de alertas.
 */
@RestController
@RequestMapping("/api/alertas-config")
@Tag(name = "Configuración de Alertas", description = "API para configurar alertas de calidad del aire")
@CrossOrigin(origins = "*")
public class AlertasConfigController {

    @Operation(summary = "Guardar configuración de alertas", 
               description = "Guarda la configuración de alertas del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuración guardada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/guardar")
    public ResponseEntity<Map<String, Object>> guardarConfiguracion(
            @RequestBody ConfiguracionAlertasDTO configuracion) {
        
        try {
            // Aquí se guardaría en base de datos
            // Por ahora devolvemos una respuesta simulada
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Configuración de alertas guardada exitosamente");
            response.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al guardar la configuración");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Operation(summary = "Obtener configuración de alertas", 
               description = "Obtiene la configuración actual de alertas del usuario")
    @GetMapping("/obtener/{userId}")
    public ResponseEntity<ConfiguracionAlertasDTO> obtenerConfiguracion(
            @Parameter(description = "ID del usuario")
            @PathVariable Long userId) {
        
        // Devolver configuración por defecto como ejemplo
        ConfiguracionAlertasDTO config = new ConfiguracionAlertasDTO();
        config.setHealthProfile("normal");
        config.setAqiThreshold(100);
        
        Map<String, Integer> pollutantThresholds = new HashMap<>();
        pollutantThresholds.put("pm25", 25);
        pollutantThresholds.put("pm10", 50);
        pollutantThresholds.put("no2", 40);
        pollutantThresholds.put("o3", 100);
        pollutantThresholds.put("co", 10000);
        config.setPollutantThresholds(pollutantThresholds);
        
        Map<String, Boolean> notifications = new HashMap<>();
        notifications.put("push", true);
        notifications.put("email", false);
        notifications.put("sms", false);
        config.setNotifications(notifications);
        
        config.setLocations(List.of("centro"));
        
        return ResponseEntity.ok(config);
    }

    @Operation(summary = "Verificar alertas activas", 
               description = "Verifica si hay alertas activas para las ubicaciones del usuario")
    @GetMapping("/verificar/{userId}")
    public ResponseEntity<List<AlertaActivaDTO>> verificarAlertas(
            @Parameter(description = "ID del usuario")
            @PathVariable Long userId) {
        
        List<AlertaActivaDTO> alertas = new ArrayList<>();
        
        // Simular algunas alertas activas
        Random random = new Random();
        String[] ubicaciones = {"centro", "cercado", "yanahuara", "cayma", "bustamante"};
        
        for (String ubicacion : ubicaciones) {
            int aqi = 50 + random.nextInt(151); // AQI entre 50 y 200
            
            if (aqi > 100) { // Solo alertar si AQI > 100
                AlertaActivaDTO alerta = new AlertaActivaDTO();
                alerta.setUbicacion(ubicacion);
                alerta.setAqi(aqi);
                alerta.setNivel(aqi > 150 ? "ALTO" : "MODERADO");
                alerta.setMensaje(generarMensajeAlerta(aqi));
                alerta.setTimestamp(LocalDateTime.now().toString());
                alertas.add(alerta);
            }
        }
        
        return ResponseEntity.ok(alertas);
    }

    @Operation(summary = "Obtener tipos de perfil de salud", 
               description = "Devuelve los tipos de perfil de salud disponibles")
    @GetMapping("/perfiles-salud")
    public ResponseEntity<List<PerfilSaludDTO>> getPerfilesSalud() {
        List<PerfilSaludDTO> perfiles = List.of(
            new PerfilSaludDTO("normal", "Normal", "Sin condiciones respiratorias especiales"),
            new PerfilSaludDTO("sensible", "Sensible", "Personas con asma, alergias o sensibilidad al aire"),
            new PerfilSaludDTO("riesgo", "Alto Riesgo", "Niños, adultos mayores, embarazadas o con enfermedades cardiovasculares")
        );
        
        return ResponseEntity.ok(perfiles);
    }

    private String generarMensajeAlerta(int aqi) {
        if (aqi <= 100) return "Calidad del aire moderada. Grupos sensibles deben tomar precauciones.";
        if (aqi <= 150) return "Calidad del aire dañina para grupos sensibles. Limitar actividades al aire libre.";
        if (aqi <= 200) return "Calidad del aire dañina. Todos deben limitar actividades prolongadas al aire libre.";
        return "Calidad del aire muy dañina. Evitar salir al aire libre.";
    }

    // DTOs
    public static class ConfiguracionAlertasDTO {
        private String healthProfile;
        private Integer aqiThreshold;
        private Map<String, Integer> pollutantThresholds;
        private Map<String, Boolean> notifications;
        private List<String> locations;

        // Getters y setters
        public String getHealthProfile() { return healthProfile; }
        public void setHealthProfile(String healthProfile) { this.healthProfile = healthProfile; }
        public Integer getAqiThreshold() { return aqiThreshold; }
        public void setAqiThreshold(Integer aqiThreshold) { this.aqiThreshold = aqiThreshold; }
        public Map<String, Integer> getPollutantThresholds() { return pollutantThresholds; }
        public void setPollutantThresholds(Map<String, Integer> pollutantThresholds) { this.pollutantThresholds = pollutantThresholds; }
        public Map<String, Boolean> getNotifications() { return notifications; }
        public void setNotifications(Map<String, Boolean> notifications) { this.notifications = notifications; }
        public List<String> getLocations() { return locations; }
        public void setLocations(List<String> locations) { this.locations = locations; }
    }

    public static class AlertaActivaDTO {
        private String ubicacion;
        private Integer aqi;
        private String nivel;
        private String mensaje;
        private String timestamp;

        // Getters y setters
        public String getUbicacion() { return ubicacion; }
        public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
        public Integer getAqi() { return aqi; }
        public void setAqi(Integer aqi) { this.aqi = aqi; }
        public String getNivel() { return nivel; }
        public void setNivel(String nivel) { this.nivel = nivel; }
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }

    public static class PerfilSaludDTO {
        private String id;
        private String nombre;
        private String descripcion;

        public PerfilSaludDTO(String id, String nombre, String descripcion) {
            this.id = id;
            this.nombre = nombre;
            this.descripcion = descripcion;
        }

        // Getters y setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    }
}
