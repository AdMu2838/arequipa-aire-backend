package com.arequipa.aire.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entidad que representa una predicción de calidad del aire.
 */
@Entity
@Table(name = "predicciones", indexes = {
    @Index(name = "idx_prediccion_estacion_fecha", columnList = "estacion_id, fecha_prediccion"),
    @Index(name = "idx_prediccion_fecha", columnList = "fecha_prediccion")
})
public class Prediccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacion_id", nullable = false)
    private Estacion estacion;

    @NotNull
    @Column(name = "fecha_prediccion", nullable = false)
    private LocalDateTime fechaPrediccion;

    @NotNull
    @Column(name = "horizonte_horas", nullable = false)
    private Integer horizonteHoras;

    // Predicciones de contaminantes (μg/m³)
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "pm25_predicho")
    private Double pm25Predicho;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "pm10_predicho")
    private Double pm10Predicho;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "no2_predicho")
    private Double no2Predicho;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "o3_predicho")
    private Double o3Predicho;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "co_predicho")
    private Double coPredicho;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "aqi_predicho")
    private Integer aqiPredicho;

    @Column(name = "categoria_aqi_predicha", length = 50)
    private String categoriaAqiPredicha;

    @Column(name = "color_aqi_predicho", length = 7)
    private String colorAqiPredicho;

    // Niveles de confianza (0.0 - 1.0)
    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1.0", inclusive = true)
    @Column(name = "confianza_pm25")
    private Double confianzaPm25;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1.0", inclusive = true)
    @Column(name = "confianza_pm10")
    private Double confianzaPm10;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1.0", inclusive = true)
    @Column(name = "confianza_global")
    private Double confianzaGlobal;

    // Metadatos del modelo
    @Column(name = "modelo_utilizado", length = 100)
    private String modeloUtilizado;

    @Column(name = "version_modelo", length = 50)
    private String versionModelo;

    @Column(name = "parametros_modelo", columnDefinition = "TEXT")
    private String parametrosModelo;

    // Estado de la predicción
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPrediccion estado = EstadoPrediccion.PENDIENTE;

    @Column(name = "error_mensaje", columnDefinition = "TEXT")
    private String errorMensaje;

    @Column(name = "fecha_calculo")
    private LocalDateTime fechaCalculo;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Enum
    public enum EstadoPrediccion {
        PENDIENTE, PROCESANDO, COMPLETADA, ERROR
    }

    // Constructors
    public Prediccion() {}

    public Prediccion(Estacion estacion, LocalDateTime fechaPrediccion, Integer horizonteHoras) {
        this.estacion = estacion;
        this.fechaPrediccion = fechaPrediccion;
        this.horizonteHoras = horizonteHoras;
        this.estado = EstadoPrediccion.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    public void marcarComoCompletada() {
        this.estado = EstadoPrediccion.COMPLETADA;
        this.fechaCalculo = LocalDateTime.now();
    }

    public void marcarComoError(String mensajeError) {
        this.estado = EstadoPrediccion.ERROR;
        this.errorMensaje = mensajeError;
        this.fechaCalculo = LocalDateTime.now();
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

    public LocalDateTime getFechaPrediccion() {
        return fechaPrediccion;
    }

    public void setFechaPrediccion(LocalDateTime fechaPrediccion) {
        this.fechaPrediccion = fechaPrediccion;
    }

    public Integer getHorizonteHoras() {
        return horizonteHoras;
    }

    public void setHorizonteHoras(Integer horizonteHoras) {
        this.horizonteHoras = horizonteHoras;
    }

    public Double getPm25Predicho() {
        return pm25Predicho;
    }

    public void setPm25Predicho(Double pm25Predicho) {
        this.pm25Predicho = pm25Predicho;
    }

    public Double getPm10Predicho() {
        return pm10Predicho;
    }

    public void setPm10Predicho(Double pm10Predicho) {
        this.pm10Predicho = pm10Predicho;
    }

    public Double getNo2Predicho() {
        return no2Predicho;
    }

    public void setNo2Predicho(Double no2Predicho) {
        this.no2Predicho = no2Predicho;
    }

    public Double getO3Predicho() {
        return o3Predicho;
    }

    public void setO3Predicho(Double o3Predicho) {
        this.o3Predicho = o3Predicho;
    }

    public Double getCoPredicho() {
        return coPredicho;
    }

    public void setCoPredicho(Double coPredicho) {
        this.coPredicho = coPredicho;
    }

    public Integer getAqiPredicho() {
        return aqiPredicho;
    }

    public void setAqiPredicho(Integer aqiPredicho) {
        this.aqiPredicho = aqiPredicho;
    }

    public String getCategoriaAqiPredicha() {
        return categoriaAqiPredicha;
    }

    public void setCategoriaAqiPredicha(String categoriaAqiPredicha) {
        this.categoriaAqiPredicha = categoriaAqiPredicha;
    }

    public String getColorAqiPredicho() {
        return colorAqiPredicho;
    }

    public void setColorAqiPredicho(String colorAqiPredicho) {
        this.colorAqiPredicho = colorAqiPredicho;
    }

    public Double getConfianzaPm25() {
        return confianzaPm25;
    }

    public void setConfianzaPm25(Double confianzaPm25) {
        this.confianzaPm25 = confianzaPm25;
    }

    public Double getConfianzaPm10() {
        return confianzaPm10;
    }

    public void setConfianzaPm10(Double confianzaPm10) {
        this.confianzaPm10 = confianzaPm10;
    }

    public Double getConfianzaGlobal() {
        return confianzaGlobal;
    }

    public void setConfianzaGlobal(Double confianzaGlobal) {
        this.confianzaGlobal = confianzaGlobal;
    }

    public String getModeloUtilizado() {
        return modeloUtilizado;
    }

    public void setModeloUtilizado(String modeloUtilizado) {
        this.modeloUtilizado = modeloUtilizado;
    }

    public String getVersionModelo() {
        return versionModelo;
    }

    public void setVersionModelo(String versionModelo) {
        this.versionModelo = versionModelo;
    }

    public String getParametrosModelo() {
        return parametrosModelo;
    }

    public void setParametrosModelo(String parametrosModelo) {
        this.parametrosModelo = parametrosModelo;
    }

    public EstadoPrediccion getEstado() {
        return estado;
    }

    public void setEstado(EstadoPrediccion estado) {
        this.estado = estado;
    }

    public String getErrorMensaje() {
        return errorMensaje;
    }

    public void setErrorMensaje(String errorMensaje) {
        this.errorMensaje = errorMensaje;
    }

    public LocalDateTime getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(LocalDateTime fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}