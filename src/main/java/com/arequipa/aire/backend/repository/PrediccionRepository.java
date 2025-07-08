package com.arequipa.aire.backend.repository;

import com.arequipa.aire.backend.entity.Prediccion;
import com.arequipa.aire.backend.entity.Estacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Prediccion.
 */
@Repository
public interface PrediccionRepository extends JpaRepository<Prediccion, Long> {

    /**
     * Encuentra predicciones por estación ordenadas por fecha de predicción.
     */
    List<Prediccion> findByEstacionOrderByFechaPrediccionAsc(Estacion estacion);

    /**
     * Encuentra predicciones por ID de estación.
     */
    @Query("SELECT p FROM Prediccion p WHERE p.estacion.id = :estacionId ORDER BY p.fechaPrediccion ASC")
    List<Prediccion> findByEstacionIdOrderByFechaPrediccionAsc(@Param("estacionId") Long estacionId);

    /**
     * Encuentra predicciones futuras por estación.
     */
    @Query("SELECT p FROM Prediccion p WHERE p.estacion = :estacion AND p.fechaPrediccion >= :fechaActual AND p.estado = 'COMPLETADA' ORDER BY p.fechaPrediccion ASC")
    List<Prediccion> findPrediccionesFuturas(@Param("estacion") Estacion estacion, @Param("fechaActual") LocalDateTime fechaActual);

    /**
     * Encuentra la predicción más reciente para una estación.
     */
    Optional<Prediccion> findTopByEstacionAndEstadoOrderByFechaPrediccionDesc(Estacion estacion, Prediccion.EstadoPrediccion estado);

    /**
     * Encuentra predicciones por estado.
     */
    List<Prediccion> findByEstadoOrderByFechaCreacionAsc(Prediccion.EstadoPrediccion estado);

    /**
     * Encuentra predicciones en un rango de fechas.
     */
    @Query("SELECT p FROM Prediccion p WHERE p.fechaPrediccion BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaPrediccion ASC")
    List<Prediccion> findByFechaPrediccionBetween(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Encuentra predicciones con alta confianza.
     */
    @Query("SELECT p FROM Prediccion p WHERE p.confianzaGlobal >= :umbralConfianza AND p.estado = 'COMPLETADA' ORDER BY p.confianzaGlobal DESC")
    List<Prediccion> findPrediccionesConfiables(@Param("umbralConfianza") Double umbralConfianza);

    /**
     * Encuentra predicciones pendientes más antiguas.
     */
    @Query("SELECT p FROM Prediccion p WHERE p.estado = 'PENDIENTE' ORDER BY p.fechaCreacion ASC")
    List<Prediccion> findPrediccionesPendientes();

    /**
     * Encuentra predicciones con errores para análisis.
     */
    List<Prediccion> findByEstadoOrderByFechaCreacionDesc(Prediccion.EstadoPrediccion estado);

    /**
     * Cuenta predicciones por estado.
     */
    long countByEstado(Prediccion.EstadoPrediccion estado);

    /**
     * Encuentra predicciones por modelo utilizado.
     */
    List<Prediccion> findByModeloUtilizadoAndEstadoOrderByFechaCreacionDesc(String modeloUtilizado, Prediccion.EstadoPrediccion estado);

    /**
     * Estadísticas de precisión por estación.
     */
    @Query("""
        SELECT p.estacion.id, p.estacion.nombre, AVG(p.confianzaGlobal), COUNT(p)
        FROM Prediccion p 
        WHERE p.estado = 'COMPLETADA' 
        AND p.fechaCreacion >= :fechaLimite
        GROUP BY p.estacion.id, p.estacion.nombre
        ORDER BY AVG(p.confianzaGlobal) DESC
        """)
    List<Object[]> findEstadisticasPrecisionPorEstacion(@Param("fechaLimite") LocalDateTime fechaLimite);

    /**
     * Elimina predicciones antiguas (para limpieza automática).
     */
    void deleteByFechaCreacionBefore(LocalDateTime fechaLimite);

    /**
     * Encuentra predicciones duplicadas para una estación y fecha.
     */
    @Query("""
        SELECT p FROM Prediccion p 
        WHERE p.estacion = :estacion 
        AND p.fechaPrediccion = :fechaPrediccion 
        AND p.horizonteHoras = :horizonteHoras
        """)
    List<Prediccion> findPrediccionesDuplicadas(
            @Param("estacion") Estacion estacion, 
            @Param("fechaPrediccion") LocalDateTime fechaPrediccion, 
            @Param("horizonteHoras") Integer horizonteHoras);

    /**
     * Encuentra todas las predicciones para el mapa (últimas disponibles).
     */
    @Query("""
        SELECT p FROM Prediccion p 
        WHERE p.fechaPrediccion = (
            SELECT MAX(p2.fechaPrediccion) 
            FROM Prediccion p2 
            WHERE p2.estacion = p.estacion 
            AND p2.estado = 'COMPLETADA'
            AND p2.fechaPrediccion >= :fechaActual
        ) 
        AND p.estado = 'COMPLETADA'
        AND p.estacion.activa = true
        """)
    List<Prediccion> findPrediccionesParaMapa(@Param("fechaActual") LocalDateTime fechaActual);
}