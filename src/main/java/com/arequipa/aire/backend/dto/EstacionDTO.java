package com.arequipa.aire.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO para estaciones.
 */
@Schema(description = "Información de estación de monitoreo")
public class EstacionDTO {

    @Schema(description = "ID de la estación", example = "1")
    private Long id;

    @Schema(description = "Nombre de la estación", example = "Estación Centro")
    private String nombre;

    @Schema(description = "Descripción", example = "Estación ubicada en el centro de Arequipa")
    private String descripcion;

    @Schema(description = "Latitud", example = "-16.4090")
    private Double latitud;

    @Schema(description = "Longitud", example = "-71.5375")
    private Double longitud;

    @Schema(description = "Distrito", example = "Cercado")
    private String distrito;

    @Schema(description = "Estación activa", example = "true")
    private Boolean activa;

    @Schema(description = "Fecha de instalación")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaInstalacion;

    @Schema(description = "Última medición disponible")
    private CalidadAireDTO ultimaMedicion;

    @Schema(description = "Estado de la estación", example = "OPERATIVA")
    private String estado;

    @Schema(description = "Cantidad de mediciones en las últimas 24 horas", example = "24")
    private Long medicionesRecientes;

    // Constructors
    public EstacionDTO() {}

    public EstacionDTO(Long id, String nombre, String descripcion, Double latitud, 
                      Double longitud, String distrito, Boolean activa) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.distrito = distrito;
        this.activa = activa;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public LocalDateTime getFechaInstalacion() {
        return fechaInstalacion;
    }

    public void setFechaInstalacion(LocalDateTime fechaInstalacion) {
        this.fechaInstalacion = fechaInstalacion;
    }

    public CalidadAireDTO getUltimaMedicion() {
        return ultimaMedicion;
    }

    public void setUltimaMedicion(CalidadAireDTO ultimaMedicion) {
        this.ultimaMedicion = ultimaMedicion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getMedicionesRecientes() {
        return medicionesRecientes;
    }

    public void setMedicionesRecientes(Long medicionesRecientes) {
        this.medicionesRecientes = medicionesRecientes;
    }
}