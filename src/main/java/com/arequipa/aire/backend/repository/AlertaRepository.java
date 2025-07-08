package com.arequipa.aire.backend.repository;

import com.arequipa.aire.backend.entity.Alerta;
import com.arequipa.aire.backend.entity.Usuario;
import com.arequipa.aire.backend.entity.Estacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Alerta.
 */
@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    /**
     * Encuentra alertas por usuario ordenadas por fecha de creación descendente.
     */
    Page<Alerta> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario, Pageable pageable);

    /**
     * Encuentra alertas no leídas por usuario.
     */
    List<Alerta> findByUsuarioAndLeidaFalseOrderByFechaCreacionDesc(Usuario usuario);

    /**
     * Encuentra alertas por usuario y tipo.
     */
    List<Alerta> findByUsuarioAndTipoOrderByFechaCreacionDesc(Usuario usuario, Alerta.TipoAlerta tipo);

    /**
     * Encuentra alertas por usuario y severidad.
     */
    List<Alerta> findByUsuarioAndSeveridadOrderByFechaCreacionDesc(Usuario usuario, Alerta.SeveridadAlerta severidad);

    /**
     * Encuentra alertas en un rango de fechas.
     */
    List<Alerta> findByFechaCreacionBetweenOrderByFechaCreacionDesc(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Cuenta alertas no leídas por usuario.
     */
    long countByUsuarioAndLeidaFalse(Usuario usuario);

    /**
     * Encuentra alertas por estación en las últimas 24 horas.
     */
    @Query("SELECT a FROM Alerta a WHERE a.estacion = :estacion AND a.fechaCreacion >= :fechaLimite ORDER BY a.fechaCreacion DESC")
    List<Alerta> findByEstacionAndFechaCreacionAfter(@Param("estacion") Estacion estacion, @Param("fechaLimite") LocalDateTime fechaLimite);

    /**
     * Encuentra alertas críticas recientes.
     */
    @Query("SELECT a FROM Alerta a WHERE a.severidad = 'CRITICA' AND a.fechaCreacion >= :fechaLimite ORDER BY a.fechaCreacion DESC")
    List<Alerta> findAlertasCriticasRecientes(@Param("fechaLimite") LocalDateTime fechaLimite);

    /**
     * Encuentra alertas de calidad del aire por umbral superado.
     */
    @Query("""
        SELECT a FROM Alerta a 
        WHERE a.tipo = 'CALIDAD_AIRE' 
        AND a.valorMedido > :umbral 
        AND a.fechaCreacion >= :fechaLimite 
        ORDER BY a.valorMedido DESC
        """)
    List<Alerta> findAlertasPorUmbralSuperado(@Param("umbral") Double umbral, @Param("fechaLimite") LocalDateTime fechaLimite);

    /**
     * Estadísticas de alertas por tipo en un período.
     */
    @Query("""
        SELECT a.tipo, COUNT(a) 
        FROM Alerta a 
        WHERE a.fechaCreacion BETWEEN :fechaInicio AND :fechaFin 
        GROUP BY a.tipo
        """)
    List<Object[]> findEstadisticasPorTipo(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Estadísticas de alertas por severidad en un período.
     */
    @Query("""
        SELECT a.severidad, COUNT(a) 
        FROM Alerta a 
        WHERE a.fechaCreacion BETWEEN :fechaInicio AND :fechaFin 
        GROUP BY a.severidad
        """)
    List<Object[]> findEstadisticasPorSeveridad(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Elimina alertas antiguas (para limpieza automática).
     */
    void deleteByFechaCreacionBefore(LocalDateTime fechaLimite);

    /**
     * Encuentra alertas duplicadas para evitar spam.
     */
    @Query("""
        SELECT a FROM Alerta a 
        WHERE a.usuario = :usuario 
        AND a.tipo = :tipo 
        AND a.contaminante = :contaminante 
        AND a.fechaCreacion >= :fechaLimite
        """)
    List<Alerta> findAlertasSimilares(
            @Param("usuario") Usuario usuario, 
            @Param("tipo") Alerta.TipoAlerta tipo, 
            @Param("contaminante") String contaminante, 
            @Param("fechaLimite") LocalDateTime fechaLimite);
}