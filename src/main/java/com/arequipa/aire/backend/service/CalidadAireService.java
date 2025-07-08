package com.arequipa.aire.backend.service;

import com.arequipa.aire.backend.dto.CalidadAireDTO;
import com.arequipa.aire.backend.entity.Estacion;
import com.arequipa.aire.backend.entity.Medicion;
import com.arequipa.aire.backend.repository.EstacionRepository;
import com.arequipa.aire.backend.repository.MedicionRepository;
import com.arequipa.aire.backend.util.AQICalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio principal para gestión de calidad del aire.
 */
@Service
@Transactional(readOnly = true)
public class CalidadAireService {

    private static final Logger logger = LoggerFactory.getLogger(CalidadAireService.class);

    @Autowired
    private MedicionRepository medicionRepository;

    @Autowired
    private EstacionRepository estacionRepository;

    @Autowired
    private AQICalculator aqiCalculator;

    /**
     * Obtiene datos actuales de todas las estaciones.
     */
    @Cacheable(value = "mediciones-actuales", key = "'todas'")
    public List<CalidadAireDTO> obtenerDatosActuales() {
        List<Medicion> mediciones = medicionRepository.findLatestMedicionesPorEstacion();
        
        return mediciones.stream()
                .map(this::convertirACalidadAireDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene datos actuales de una estación específica.
     */
    @Cacheable(value = "mediciones-actuales", key = "#estacionId")
    public CalidadAireDTO obtenerDatosActualesEstacion(Long estacionId) {
        Medicion medicion = medicionRepository.findLatestByEstacionId(estacionId)
                .orElseThrow(() -> new RuntimeException("No hay datos disponibles para la estación: " + estacionId));

        return convertirACalidadAireDTO(medicion);
    }

    /**
     * Obtiene datos históricos de una estación con paginación.
     */
    public Page<CalidadAireDTO> obtenerDatosHistoricos(Long estacionId, LocalDateTime fechaInicio, 
                                                      LocalDateTime fechaFin, Pageable pageable) {
        
        Page<Medicion> mediciones = medicionRepository.findByEstacionIdAndFechaMedicionBetween(
                estacionId, fechaInicio, fechaFin, pageable);

        return mediciones.map(this::convertirACalidadAireDTO);
    }

    /**
     * Obtiene datos optimizados para visualización en mapa.
     */
    @Cacheable(value = "mediciones-mapa", key = "'actual'")
    public List<CalidadAireDTO> obtenerDatosParaMapa() {
        List<Medicion> mediciones = medicionRepository.findMedicionesParaMapa();
        
        return mediciones.stream()
                .map(this::convertirACalidadAireDTOSimple)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene mediciones recientes (últimas N horas).
     */
    public List<CalidadAireDTO> obtenerMedicionesRecientes(int horas) {
        LocalDateTime fechaLimite = LocalDateTime.now().minusHours(horas);
        List<Medicion> mediciones = medicionRepository.findMedicionesRecientes(fechaLimite);
        
        return mediciones.stream()
                .map(this::convertirACalidadAireDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene estadísticas de calidad del aire por distrito.
     */
    public List<EstadisticaDistritoDTO> obtenerEstadisticasPorDistrito() {
        LocalDateTime hace24Horas = LocalDateTime.now().minusHours(24);
        List<Object[]> resultados = medicionRepository.findPromedioAqiPorDistrito(hace24Horas);
        
        return resultados.stream()
                .map(row -> {
                    EstadisticaDistritoDTO dto = new EstadisticaDistritoDTO();
                    dto.setDistrito((String) row[0]);
                    dto.setPromedioAqi(((Number) row[1]).doubleValue());
                    dto.setCantidadMediciones(((Number) row[2]).longValue());
                    
                    // Determinar categoría y color basado en AQI promedio
                    AQICalculator.AQIInfo aqiInfo = aqiCalculator.calcularAQI(
                            dto.getPromedioAqi(), null, null, null, null);
                    dto.setCategoria(aqiInfo.getCategoria());
                    dto.setColor(aqiInfo.getColor());
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Busca mediciones con AQI alto.
     */
    public List<CalidadAireDTO> buscarMedicionesAqiAlto(int umbralAqi, int horasAtras) {
        LocalDateTime fechaLimite = LocalDateTime.now().minusHours(horasAtras);
        List<Medicion> mediciones = medicionRepository.findMedicionesConAqiAlto(umbralAqi, fechaLimite);
        
        return mediciones.stream()
                .map(this::convertirACalidadAireDTO)
                .collect(Collectors.toList());
    }

    /**
     * Guarda una nueva medición y calcula AQI.
     */
    @Transactional
    public CalidadAireDTO guardarMedicion(CalidadAireDTO calidadAireDTO) {
        Estacion estacion = estacionRepository.findById(calidadAireDTO.getEstacionId())
                .orElseThrow(() -> new RuntimeException("Estación no encontrada: " + calidadAireDTO.getEstacionId()));

        Medicion medicion = new Medicion(estacion, calidadAireDTO.getFechaMedicion());
        
        // Establecer valores de contaminantes
        medicion.setPm25(calidadAireDTO.getPm25());
        medicion.setPm10(calidadAireDTO.getPm10());
        medicion.setNo2(calidadAireDTO.getNo2());
        medicion.setO3(calidadAireDTO.getO3());
        medicion.setCo(calidadAireDTO.getCo());
        medicion.setSo2(calidadAireDTO.getSo2());
        
        // Establecer datos meteorológicos
        medicion.setTemperatura(calidadAireDTO.getTemperatura());
        medicion.setHumedad(calidadAireDTO.getHumedad());
        medicion.setPresion(calidadAireDTO.getPresion());
        medicion.setVelocidadViento(calidadAireDTO.getVelocidadViento());
        medicion.setDireccionViento(calidadAireDTO.getDireccionViento());
        
        // Calcular AQI
        AQICalculator.AQIInfo aqiInfo = aqiCalculator.calcularAQI(
                medicion.getPm25(), medicion.getPm10(), medicion.getNo2(),
                medicion.getO3(), medicion.getCo());
        
        medicion.setAqi(aqiInfo.getAqi());
        medicion.setCategoriaAqi(aqiInfo.getCategoria());
        medicion.setColorAqi(aqiInfo.getColor());
        
        // Metadatos
        medicion.setFuenteDatos(calidadAireDTO.getFuenteDatos());
        medicion.setConfiabilidad(calidadAireDTO.getConfiabilidad());

        Medicion medicionGuardada = medicionRepository.save(medicion);
        
        logger.info("Nueva medición guardada para estación {}: AQI = {}", 
                   estacion.getNombre(), aqiInfo.getAqi());

        return convertirACalidadAireDTO(medicionGuardada);
    }

    /**
     * Convierte una entidad Medicion a DTO completo.
     */
    private CalidadAireDTO convertirACalidadAireDTO(Medicion medicion) {
        CalidadAireDTO dto = new CalidadAireDTO();
        
        // Información de la estación
        dto.setEstacionId(medicion.getEstacion().getId());
        dto.setEstacionNombre(medicion.getEstacion().getNombre());
        dto.setDistrito(medicion.getEstacion().getDistrito());
        dto.setLatitud(medicion.getEstacion().getLatitud());
        dto.setLongitud(medicion.getEstacion().getLongitud());
        
        // Datos temporales
        dto.setFechaMedicion(medicion.getFechaMedicion());
        
        // Contaminantes
        dto.setPm25(medicion.getPm25());
        dto.setPm10(medicion.getPm10());
        dto.setNo2(medicion.getNo2());
        dto.setO3(medicion.getO3());
        dto.setCo(medicion.getCo());
        dto.setSo2(medicion.getSo2());
        
        // AQI
        dto.setAqi(medicion.getAqi());
        dto.setCategoriaAqi(medicion.getCategoriaAqi());
        dto.setColorAqi(medicion.getColorAqi());
        
        // Recomendación basada en AQI
        if (medicion.getAqi() != null) {
            AQICalculator.AQIInfo aqiInfo = aqiCalculator.calcularAQI(
                    medicion.getPm25(), medicion.getPm10(), medicion.getNo2(),
                    medicion.getO3(), medicion.getCo());
            dto.setRecomendacion(aqiInfo.getRecomendacion());
        }
        
        // Datos meteorológicos
        dto.setTemperatura(medicion.getTemperatura());
        dto.setHumedad(medicion.getHumedad());
        dto.setPresion(medicion.getPresion());
        dto.setVelocidadViento(medicion.getVelocidadViento());
        dto.setDireccionViento(medicion.getDireccionViento());
        
        // Metadatos
        dto.setFuenteDatos(medicion.getFuenteDatos());
        dto.setConfiabilidad(medicion.getConfiabilidad());
        
        return dto;
    }

    /**
     * Convierte una entidad Medicion a DTO simple para mapas.
     */
    private CalidadAireDTO convertirACalidadAireDTOSimple(Medicion medicion) {
        CalidadAireDTO dto = new CalidadAireDTO();
        
        dto.setEstacionId(medicion.getEstacion().getId());
        dto.setEstacionNombre(medicion.getEstacion().getNombre());
        dto.setLatitud(medicion.getEstacion().getLatitud());
        dto.setLongitud(medicion.getEstacion().getLongitud());
        dto.setDistrito(medicion.getEstacion().getDistrito());
        dto.setFechaMedicion(medicion.getFechaMedicion());
        dto.setAqi(medicion.getAqi());
        dto.setCategoriaAqi(medicion.getCategoriaAqi());
        dto.setColorAqi(medicion.getColorAqi());
        dto.setPm25(medicion.getPm25());
        dto.setPm10(medicion.getPm10());
        
        return dto;
    }

    /**
     * DTO para estadísticas por distrito.
     */
    public static class EstadisticaDistritoDTO {
        private String distrito;
        private Double promedioAqi;
        private Long cantidadMediciones;
        private String categoria;
        private String color;

        // Getters and Setters
        public String getDistrito() { return distrito; }
        public void setDistrito(String distrito) { this.distrito = distrito; }
        public Double getPromedioAqi() { return promedioAqi; }
        public void setPromedioAqi(Double promedioAqi) { this.promedioAqi = promedioAqi; }
        public Long getCantidadMediciones() { return cantidadMediciones; }
        public void setCantidadMediciones(Long cantidadMediciones) { this.cantidadMediciones = cantidadMediciones; }
        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }
}