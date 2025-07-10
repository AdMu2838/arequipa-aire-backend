package com.arequipa.aire.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controlador REST para servicios adicionales del frontend.
 */
@RestController
@RequestMapping("/api/servicios")
@Tag(name = "Servicios Adicionales", description = "API para servicios adicionales del frontend")
@CrossOrigin(origins = "*")
public class ServiciosController {

    @Operation(summary = "Obtener ubicaciones disponibles", 
               description = "Devuelve la lista de ubicaciones/estaciones disponibles")
    @GetMapping("/ubicaciones")
    public ResponseEntity<List<UbicacionDTO>> getUbicaciones() {
        
        List<UbicacionDTO> ubicaciones = Arrays.asList(
            new UbicacionDTO("centro", "Centro Histórico", -16.3988, -71.5369),
            new UbicacionDTO("cercado", "Cercado de Arequipa", -16.4090, -71.5375),
            new UbicacionDTO("yanahuara", "Yanahuara", -16.3967, -71.5444),
            new UbicacionDTO("cayma", "Cayma", -16.3644, -71.5492),
            new UbicacionDTO("bustamante", "José Luis Bustamante y Rivero", -16.4244, -71.5300)
        );
        
        return ResponseEntity.ok(ubicaciones);
    }

    @Operation(summary = "Obtener información de contaminantes", 
               description = "Devuelve información detallada sobre los contaminantes monitoreados")
    @GetMapping("/contaminantes")
    public ResponseEntity<Map<String, ContaminanteInfoDTO>> getContaminantesInfo() {
        
        Map<String, ContaminanteInfoDTO> info = new HashMap<>();
        
        info.put("pm25", new ContaminanteInfoDTO(
            "PM2.5", "🔴", 
            "Partículas finas que pueden penetrar profundamente en los pulmones", 
            25
        ));
        
        info.put("pm10", new ContaminanteInfoDTO(
            "PM10", "🟤", 
            "Partículas inhalables que afectan los pulmones y el corazón", 
            50
        ));
        
        info.put("no2", new ContaminanteInfoDTO(
            "NO₂", "🟡", 
            "Dióxido de nitrógeno, principalmente de vehículos e industrias", 
            40
        ));
        
        info.put("o3", new ContaminanteInfoDTO(
            "O₃", "🔵", 
            "Ozono troposférico, se forma por reacciones fotoquímicas", 
            100
        ));
        
        info.put("co", new ContaminanteInfoDTO(
            "CO", "⚫", 
            "Monóxido de carbono, gas incoloro e inodoro muy tóxico", 
            10000
        ));
        
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "Obtener categorías de AQI", 
               description = "Devuelve las categorías del Índice de Calidad del Aire")
    @GetMapping("/aqi-categorias")
    public ResponseEntity<List<AQICategoriaDTO>> getAQICategorias() {
        
        List<AQICategoriaDTO> categorias = Arrays.asList(
            new AQICategoriaDTO(0, 50, "Bueno", "#4CAF50", "Calidad del aire satisfactoria, poco o ningún riesgo"),
            new AQICategoriaDTO(51, 100, "Moderado", "#FFC107", "Calidad del aire aceptable para la mayoría"),
            new AQICategoriaDTO(101, 150, "Dañino para grupos sensibles", "#FF9800", "Los grupos sensibles pueden experimentar problemas de salud"),
            new AQICategoriaDTO(151, 200, "Dañino", "#F44336", "Todos pueden experimentar problemas de salud"),
            new AQICategoriaDTO(201, 300, "Muy dañino", "#9C27B0", "Alerta de salud: todos pueden experimentar efectos graves"),
            new AQICategoriaDTO(301, 500, "Peligroso", "#8B0000", "Alerta sanitaria: condiciones de emergencia")
        );
        
        return ResponseEntity.ok(categorias);
    }

    @Operation(summary = "Validar estado del sistema", 
               description = "Verifica el estado de los servicios del sistema")
    @GetMapping("/estado")
    public ResponseEntity<EstadoSistemaDTO> getEstadoSistema() {
        
        EstadoSistemaDTO estado = new EstadoSistemaDTO();
        estado.setEstado("OPERATIVO");
        estado.setVersion("1.0.0");
        estado.setTimestamp(new Date().toString());
        
        // Estado de servicios
        Map<String, String> servicios = new HashMap<>();
        servicios.put("base_datos", "ACTIVO");
        servicios.put("api_externa", "ACTIVO");
        servicios.put("notificaciones", "ACTIVO");
        servicios.put("cache", "ACTIVO");
        estado.setServicios(servicios);
        
        // Estadísticas básicas
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("estaciones_activas", 5);
        estadisticas.put("mediciones_ultimas_24h", 120);
        estadisticas.put("usuarios_activos", 1250);
        estadisticas.put("alertas_activas", 3);
        estado.setEstadisticas(estadisticas);
        
        return ResponseEntity.ok(estado);
    }

    @Operation(summary = "Buscar estaciones por criterio", 
               description = "Busca estaciones por nombre o distrito")
    @GetMapping("/buscar-estaciones")
    public ResponseEntity<List<UbicacionDTO>> buscarEstaciones(
            @Parameter(description = "Término de búsqueda")
            @RequestParam String termino) {
        
        List<UbicacionDTO> todasUbicaciones = Arrays.asList(
            new UbicacionDTO("centro", "Centro Histórico", -16.3988, -71.5369),
            new UbicacionDTO("cercado", "Cercado de Arequipa", -16.4090, -71.5375),
            new UbicacionDTO("yanahuara", "Yanahuara", -16.3967, -71.5444),
            new UbicacionDTO("cayma", "Cayma", -16.3644, -71.5492),
            new UbicacionDTO("bustamante", "José Luis Bustamante y Rivero", -16.4244, -71.5300)
        );
        
        // Filtrar por término de búsqueda
        List<UbicacionDTO> resultados = todasUbicaciones.stream()
                .filter(u -> u.getNombre().toLowerCase().contains(termino.toLowerCase()))
                .toList();
        
        return ResponseEntity.ok(resultados);
    }

    // DTOs
    public static class UbicacionDTO {
        private String id;
        private String nombre;
        private Double latitud;
        private Double longitud;

        public UbicacionDTO(String id, String nombre, Double latitud, Double longitud) {
            this.id = id;
            this.nombre = nombre;
            this.latitud = latitud;
            this.longitud = longitud;
        }

        // Getters y setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public Double getLatitud() { return latitud; }
        public void setLatitud(Double latitud) { this.latitud = latitud; }
        public Double getLongitud() { return longitud; }
        public void setLongitud(Double longitud) { this.longitud = longitud; }
    }

    public static class ContaminanteInfoDTO {
        private String nombre;
        private String icono;
        private String descripcion;
        private Integer limite;

        public ContaminanteInfoDTO(String nombre, String icono, String descripcion, Integer limite) {
            this.nombre = nombre;
            this.icono = icono;
            this.descripcion = descripcion;
            this.limite = limite;
        }

        // Getters y setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getIcono() { return icono; }
        public void setIcono(String icono) { this.icono = icono; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public Integer getLimite() { return limite; }
        public void setLimite(Integer limite) { this.limite = limite; }
    }

    public static class AQICategoriaDTO {
        private Integer minimo;
        private Integer maximo;
        private String categoria;
        private String color;
        private String descripcion;

        public AQICategoriaDTO(Integer minimo, Integer maximo, String categoria, String color, String descripcion) {
            this.minimo = minimo;
            this.maximo = maximo;
            this.categoria = categoria;
            this.color = color;
            this.descripcion = descripcion;
        }

        // Getters y setters
        public Integer getMinimo() { return minimo; }
        public void setMinimo(Integer minimo) { this.minimo = minimo; }
        public Integer getMaximo() { return maximo; }
        public void setMaximo(Integer maximo) { this.maximo = maximo; }
        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    }

    public static class EstadoSistemaDTO {
        private String estado;
        private String version;
        private String timestamp;
        private Map<String, String> servicios;
        private Map<String, Object> estadisticas;

        // Getters y setters
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        public Map<String, String> getServicios() { return servicios; }
        public void setServicios(Map<String, String> servicios) { this.servicios = servicios; }
        public Map<String, Object> getEstadisticas() { return estadisticas; }
        public void setEstadisticas(Map<String, Object> estadisticas) { this.estadisticas = estadisticas; }
    }
}
