package com.arequipa.aire.backend.repository;

import com.arequipa.aire.backend.entity.Estacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Estacion.
 */
@Repository
public interface EstacionRepository extends JpaRepository<Estacion, Long> {

    /**
     * Encuentra todas las estaciones activas.
     */
    List<Estacion> findByActivaTrue();

    /**
     * Encuentra estaciones por distrito.
     */
    List<Estacion> findByDistritoAndActivaTrue(String distrito);

    /**
     * Encuentra una estación por nombre.
     */
    Optional<Estacion> findByNombreAndActivaTrue(String nombre);

    /**
     * Encuentra estaciones dentro de un radio específico (en km) de una ubicación.
     */
    @Query(value = """
        SELECT e.* FROM estaciones e 
        WHERE e.activa = true 
        AND (6371 * acos(cos(radians(:latitud)) * cos(radians(e.latitud)) * 
             cos(radians(e.longitud) - radians(:longitud)) + 
             sin(radians(:latitud)) * sin(radians(e.latitud)))) <= :radioKm
        ORDER BY (6371 * acos(cos(radians(:latitud)) * cos(radians(e.latitud)) * 
                  cos(radians(e.longitud) - radians(:longitud)) + 
                  sin(radians(:latitud)) * sin(radians(e.latitud))))
        """, nativeQuery = true)
    List<Estacion> findEstacionesCercanas(Double latitud, Double longitud, Double radioKm);

    /**
     * Cuenta el total de estaciones activas.
     */
    long countByActivaTrue();

    /**
     * Encuentra todos los distritos únicos con estaciones activas.
     */
    @Query("SELECT DISTINCT e.distrito FROM Estacion e WHERE e.activa = true AND e.distrito IS NOT NULL ORDER BY e.distrito")
    List<String> findDistritosConEstaciones();
}