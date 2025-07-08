package com.arequipa.aire.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entidad que representa una medición de calidad del aire.
 */
@Entity
@Table(name = "mediciones", indexes = {
    @Index(name = "idx_medicion_estacion_fecha", columnList = "estacion_id, fecha_medicion"),
    @Index(name = "idx_medicion_fecha", columnList = "fecha_medicion")
})
public class Medicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacion_id", nullable = false)
    private Estacion estacion;

    @NotNull
    @Column(name = "fecha_medicion", nullable = false)
    private LocalDateTime fechaMedicion;

    // Contaminantes principales (μg/m³)
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "pm25")
    private Double pm25;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "pm10")
    private Double pm10;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "no2")
    private Double no2;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "o3")
    private Double o3;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "co")
    private Double co;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "so2")
    private Double so2;

    // Índice de Calidad del Aire
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "aqi")
    private Integer aqi;

    @Column(name = "categoria_aqi", length = 50)
    private String categoriaAqi;

    @Column(name = "color_aqi", length = 7)
    private String colorAqi;

    // Datos meteorológicos
    @Column(name = "temperatura")
    private Double temperatura;

    @Column(name = "humedad")
    private Integer humedad;

    @Column(name = "presion")
    private Double presion;

    @Column(name = "velocidad_viento")
    private Double velocidadViento;

    @Column(name = "direccion_viento")
    private Integer direccionViento;

    // Metadatos
    @Column(name = "fuente_datos", length = 50)
    private String fuenteDatos;

    @Column(name = "confiabilidad")
    private Double confiabilidad;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Constructors
    public Medicion() {}

    public Medicion(Estacion estacion, LocalDateTime fechaMedicion) {
        this.estacion = estacion;
        this.fechaMedicion = fechaMedicion;
        this.fechaCreacion = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public void setEstacion(Estacion estacion) {
        this.estacion = estacion;
    }

    public LocalDateTime getFechaMedicion() {
        return fechaMedicion;
    }

    public void setFechaMedicion(LocalDateTime fechaMedicion) {
        this.fechaMedicion = fechaMedicion;
    }

    public Double getPm25() {
        return pm25;
    }

    public void setPm25(Double pm25) {
        this.pm25 = pm25;
    }

    public Double getPm10() {
        return pm10;
    }

    public void setPm10(Double pm10) {
        this.pm10 = pm10;
    }

    public Double getNo2() {
        return no2;
    }

    public void setNo2(Double no2) {
        this.no2 = no2;
    }

    public Double getO3() {
        return o3;
    }

    public void setO3(Double o3) {
        this.o3 = o3;
    }

    public Double getCo() {
        return co;
    }

    public void setCo(Double co) {
        this.co = co;
    }

    public Double getSo2() {
        return so2;
    }

    public void setSo2(Double so2) {
        this.so2 = so2;
    }

    public Integer getAqi() {
        return aqi;
    }

    public void setAqi(Integer aqi) {
        this.aqi = aqi;
    }

    public String getCategoriaAqi() {
        return categoriaAqi;
    }

    public void setCategoriaAqi(String categoriaAqi) {
        this.categoriaAqi = categoriaAqi;
    }

    public String getColorAqi() {
        return colorAqi;
    }

    public void setColorAqi(String colorAqi) {
        this.colorAqi = colorAqi;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Integer getHumedad() {
        return humedad;
    }

    public void setHumedad(Integer humedad) {
        this.humedad = humedad;
    }

    public Double getPresion() {
        return presion;
    }

    public void setPresion(Double presion) {
        this.presion = presion;
    }

    public Double getVelocidadViento() {
        return velocidadViento;
    }

    public void setVelocidadViento(Double velocidadViento) {
        this.velocidadViento = velocidadViento;
    }

    public Integer getDireccionViento() {
        return direccionViento;
    }

    public void setDireccionViento(Integer direccionViento) {
        this.direccionViento = direccionViento;
    }

    public String getFuenteDatos() {
        return fuenteDatos;
    }

    public void setFuenteDatos(String fuenteDatos) {
        this.fuenteDatos = fuenteDatos;
    }

    public Double getConfiabilidad() {
        return confiabilidad;
    }

    public void setConfiabilidad(Double confiabilidad) {
        this.confiabilidad = confiabilidad;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}