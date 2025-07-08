package com.arequipa.aire.backend.repository;

import com.arequipa.aire.backend.entity.Medicion;
import com.arequipa.aire.backend.entity.Estacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Medicion.
 */
@Repository
public interface MedicionRepository extends JpaRepository<Medicion, Long> {

    /**
     * Encuentra la medición más reciente por estación.
     */
    Optional<Medicion> findTopByEstacionOrderByFechaMedicionDesc(Estacion estacion);

    /**
     * Encuentra la medición más reciente por ID de estación.
     */
    @Query("SELECT m FROM Medicion m WHERE m.estacion.id = :estacionId ORDER BY m.fechaMedicion DESC LIMIT 1")
    Optional<Medicion> findLatestByEstacionId(@Param("estacionId") Long estacionId);

    /**
     * Encuentra las mediciones más recientes de todas las estaciones activas.
     */
    @Query("""
        SELECT m FROM Medicion m 
        WHERE m.fechaMedicion = (
            SELECT MAX(m2.fechaMedicion) 
            FROM Medicion m2 
            WHERE m2.estacion = m.estacion
        ) AND m.estacion.activa = true
        ORDER BY m.estacion.nombre
        """)
    List<Medicion> findLatestMedicionesPorEstacion();

    /**
     * Encuentra mediciones por estación en un rango de fechas.
     */
    Page<Medicion> findByEstacionAndFechaMedicionBetweenOrderByFechaMedicionDesc(
            Estacion estacion, LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);

    /**
     * Encuentra mediciones por ID de estación en un rango de fechas.
     */
    @Query("SELECT m FROM Medicion m WHERE m.estacion.id = :estacionId AND m.fechaMedicion BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fechaMedicion DESC")
    Page<Medicion> findByEstacionIdAndFechaMedicionBetween(
            @Param("estacionId") Long estacionId, 
            @Param("fechaInicio") LocalDateTime fechaInicio, 
            @Param("fechaFin") LocalDateTime fechaFin, 
            Pageable pageable);

    /**
     * Encuentra mediciones recientes (últimas N horas).
     */
    @Query("SELECT m FROM Medicion m WHERE m.fechaMedicion >= :fechaLimite AND m.estacion.activa = true ORDER BY m.fechaMedicion DESC")
    List<Medicion> findMedicionesRecientes(@Param("fechaLimite") LocalDateTime fechaLimite);

    /**
     * Encuentra mediciones con AQI superior a un umbral.
     */
    @Query("SELECT m FROM Medicion m WHERE m.aqi > :umbralAqi AND m.fechaMedicion >= :fechaLimite ORDER BY m.aqi DESC")
    List<Medicion> findMedicionesConAqiAlto(@Param("umbralAqi") Integer umbralAqi, @Param("fechaLimite") LocalDateTime fechaLimite);

    /**
     * Calcula el promedio de AQI por distrito en las últimas 24 horas.
     */
    @Query("""
        SELECT e.distrito, AVG(m.aqi) as promedioAqi, COUNT(m) as cantidadMediciones
        FROM Medicion m 
        JOIN m.estacion e 
        WHERE m.fechaMedicion >= :fechaLimite 
        AND e.activa = true 
        AND e.distrito IS NOT NULL
        AND m.aqi IS NOT NULL
        GROUP BY e.distrito
        ORDER BY promedioAqi DESC
        """)
    List<Object[]> findPromedioAqiPorDistrito(@Param("fechaLimite") LocalDateTime fechaLimite);

    /**
     * Encuentra mediciones para mapas (datos optimizados).
     */
    @Query("""
        SELECT m FROM Medicion m 
        WHERE m.fechaMedicion = (
            SELECT MAX(m2.fechaMedicion) 
            FROM Medicion m2 
            WHERE m2.estacion = m.estacion
        ) 
        AND m.estacion.activa = true
        AND m.aqi IS NOT NULL
        """)
    List<Medicion> findMedicionesParaMapa();

    /**
     * Elimina mediciones antiguas (para limpieza automática).
     */
    void deleteByFechaMedicionBefore(LocalDateTime fechaLimite);

    /**
     * Cuenta mediciones por estación en un período.
     */
    @Query("SELECT COUNT(m) FROM Medicion m WHERE m.estacion.id = :estacionId AND m.fechaMedicion BETWEEN :fechaInicio AND :fechaFin")
    long countByEstacionIdAndFechaMedicionBetween(
            @Param("estacionId") Long estacionId, 
            @Param("fechaInicio") LocalDateTime fechaInicio, 
            @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Encuentra el rango de fechas disponibles para una estación.
     */
    @Query("SELECT MIN(m.fechaMedicion), MAX(m.fechaMedicion) FROM Medicion m WHERE m.estacion.id = :estacionId")
    Object[] findRangoFechasByEstacionId(@Param("estacionId") Long estacionId);
}