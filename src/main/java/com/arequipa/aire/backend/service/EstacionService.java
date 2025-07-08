package com.arequipa.aire.backend.service;

import com.arequipa.aire.backend.dto.EstacionDTO;
import com.arequipa.aire.backend.entity.Estacion;
import com.arequipa.aire.backend.repository.EstacionRepository;
import com.arequipa.aire.backend.repository.MedicionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de estaciones.
 */
@Service
@Transactional(readOnly = true)
public class EstacionService {

    private static final Logger logger = LoggerFactory.getLogger(EstacionService.class);

    @Autowired
    private EstacionRepository estacionRepository;

    @Autowired
    private MedicionRepository medicionRepository;

    @Autowired
    private CalidadAireService calidadAireService;

    /**
     * Obtiene todas las estaciones activas.
     */
    @Cacheable(value = "estaciones", key = "'todas'")
    public List<EstacionDTO> obtenerTodasLasEstaciones() {
        List<Estacion> estaciones = estacionRepository.findByActivaTrue();
        
        return estaciones.stream()
                .map(this::convertirAEstacionDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una estación por ID.
     */
    @Cacheable(value = "estaciones", key = "#id")
    public Optional<EstacionDTO> obtenerEstacionPorId(Long id) {
        return estacionRepository.findById(id)
                .filter(Estacion::getActiva)
                .map(this::convertirAEstacionDTOCompleto);
    }

    /**
     * Obtiene estaciones por distrito.
     */
    public List<EstacionDTO> obtenerEstacionesPorDistrito(String distrito) {
        List<Estacion> estaciones = estacionRepository.findByDistritoAndActivaTrue(distrito);
        
        return estaciones.stream()
                .map(this::convertirAEstacionDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene estaciones cercanas a una ubicación.
     */
    public List<EstacionDTO> obtenerEstacionesCercanas(Double latitud, Double longitud, Double radioKm) {
        List<Estacion> estaciones = estacionRepository.findEstacionesCercanas(latitud, longitud, radioKm);
        
        return estaciones.stream()
                .map(this::convertirAEstacionDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los distritos con estaciones.
     */
    @Cacheable(value = "estaciones", key = "'distritos'")
    public List<String> obtenerDistritos() {
        return estacionRepository.findDistritosConEstaciones();
    }

    /**
     * Obtiene estadísticas generales de estaciones.
     */
    public EstacionStatsDTO obtenerEstadisticas() {
        long totalEstaciones = estacionRepository.countByActivaTrue();
        List<String> distritos = obtenerDistritos();
        
        EstacionStatsDTO stats = new EstacionStatsDTO();
        stats.setTotalEstaciones(totalEstaciones);
        stats.setCantidadDistritos(distritos.size());
        stats.setDistritos(distritos);
        
        return stats;
    }

    /**
     * Busca estaciones por nombre.
     */
    public List<EstacionDTO> buscarEstacionesPorNombre(String nombre) {
        List<Estacion> todasEstaciones = estacionRepository.findByActivaTrue();
        
        return todasEstaciones.stream()
                .filter(estacion -> estacion.getNombre().toLowerCase()
                                   .contains(nombre.toLowerCase()))
                .map(this::convertirAEstacionDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad Estacion a DTO básico.
     */
    private EstacionDTO convertirAEstacionDTO(Estacion estacion) {
        EstacionDTO dto = new EstacionDTO();
        dto.setId(estacion.getId());
        dto.setNombre(estacion.getNombre());
        dto.setDescripcion(estacion.getDescripcion());
        dto.setLatitud(estacion.getLatitud());
        dto.setLongitud(estacion.getLongitud());
        dto.setDistrito(estacion.getDistrito());
        dto.setActiva(estacion.getActiva());
        dto.setFechaInstalacion(estacion.getFechaInstalacion());
        dto.setEstado(determinarEstadoEstacion(estacion));
        
        return dto;
    }

    /**
     * Convierte una entidad Estacion a DTO completo con última medición.
     */
    private EstacionDTO convertirAEstacionDTOCompleto(Estacion estacion) {
        EstacionDTO dto = convertirAEstacionDTO(estacion);
        
        // Agregar última medición
        try {
            dto.setUltimaMedicion(calidadAireService.obtenerDatosActualesEstacion(estacion.getId()));
        } catch (Exception e) {
            logger.warn("No se pudo obtener última medición para estación {}: {}", 
                       estacion.getId(), e.getMessage());
        }
        
        // Agregar cantidad de mediciones recientes
        LocalDateTime hace24Horas = LocalDateTime.now().minusHours(24);
        dto.setMedicionesRecientes(medicionRepository.countByEstacionIdAndFechaMedicionBetween(
                estacion.getId(), hace24Horas, LocalDateTime.now()));
        
        return dto;
    }

    /**
     * Determina el estado operativo de una estación.
     */
    private String determinarEstadoEstacion(Estacion estacion) {
        if (!estacion.getActiva()) {
            return "INACTIVA";
        }
        
        // Verificar si hay mediciones recientes (últimas 2 horas)
        LocalDateTime hace2Horas = LocalDateTime.now().minusHours(2);
        Optional<Object[]> rangoFechas = Optional.ofNullable(
                medicionRepository.findRangoFechasByEstacionId(estacion.getId()));
        
        if (rangoFechas.isPresent() && rangoFechas.get()[1] != null) {
            LocalDateTime ultimaMedicion = (LocalDateTime) rangoFechas.get()[1];
            if (ultimaMedicion.isAfter(hace2Horas)) {
                return "OPERATIVA";
            } else if (ultimaMedicion.isAfter(LocalDateTime.now().minusDays(1))) {
                return "RETRASO";
            } else {
                return "SIN_DATOS";
            }
        }
        
        return "SIN_DATOS";
    }

    /**
     * DTO para estadísticas de estaciones.
     */
    public static class EstacionStatsDTO {
        private Long totalEstaciones;
        private Integer cantidadDistritos;
        private List<String> distritos;

        // Getters and Setters
        public Long getTotalEstaciones() { return totalEstaciones; }
        public void setTotalEstaciones(Long totalEstaciones) { this.totalEstaciones = totalEstaciones; }
        public Integer getCantidadDistritos() { return cantidadDistritos; }
        public void setCantidadDistritos(Integer cantidadDistritos) { this.cantidadDistritos = cantidadDistritos; }
        public List<String> getDistritos() { return distritos; }
        public void setDistritos(List<String> distritos) { this.distritos = distritos; }
    }
}