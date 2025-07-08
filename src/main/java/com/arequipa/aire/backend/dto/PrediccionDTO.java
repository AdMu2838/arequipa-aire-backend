package com.arequipa.aire.backend.dto;

import com.arequipa.aire.backend.entity.Prediccion;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO para predicciones.
 */
@Schema(description = "Predicción de calidad del aire")
public class PrediccionDTO {

    @Schema(description = "ID de la predicción", example = "1")
    private Long id;

    @Schema(description = "ID de la estación", example = "1")
    private Long estacionId;

    @Schema(description = "Nombre de la estación", example = "Estación Centro")
    private String estacionNombre;

    @Schema(description = "Fecha y hora de la predicción")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaPrediccion;

    @Schema(description = "Horizonte de predicción en horas", example = "24")
    private Integer horizonteHoras;

    // Predicciones de contaminantes
    @Schema(description = "Predicción PM2.5 (μg/m³)", example = "35.5")
    private Double pm25Predicho;

    @Schema(description = "Predicción PM10 (μg/m³)", example = "45.2")
    private Double pm10Predicho;

    @Schema(description = "Predicción NO₂ (μg/m³)", example = "25.1")
    private Double no2Predicho;

    @Schema(description = "Predicción O₃ (μg/m³)", example = "80.3")
    private Double o3Predicho;

    @Schema(description = "Predicción CO (μg/m³)", example = "1200.5")
    private Double coPredicho;

    @Schema(description = "AQI predicho", example = "85")
    private Integer aqiPredicho;

    @Schema(description = "Categoría AQI predicha", example = "Moderada")
    private String categoriaAqiPredicha;

    @Schema(description = "Color AQI predicho", example = "#FFFF00")
    private String colorAqiPredicho;

    // Niveles de confianza
    @Schema(description = "Confianza PM2.5 (0-1)", example = "0.85")
    private Double confianzaPm25;

    @Schema(description = "Confianza PM10 (0-1)", example = "0.78")
    private Double confianzaPm10;

    @Schema(description = "Confianza global (0-1)", example = "0.82")
    private Double confianzaGlobal;

    // Metadatos del modelo
    @Schema(description = "Modelo utilizado", example = "RandomForest_v2.1")
    private String modeloUtilizado;

    @Schema(description = "Versión del modelo", example = "2.1.0")
    private String versionModelo;

    @Schema(description = "Estado de la predicción", example = "COMPLETADA")
    private Prediccion.EstadoPrediccion estado;

    @Schema(description = "Mensaje de error (si aplica)")
    private String errorMensaje;

    @Schema(description = "Fecha de cálculo")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCalculo;

    @Schema(description = "Fecha de creación")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;

    // Constructors
    public PrediccionDTO() {}

    public PrediccionDTO(Long id, Long estacionId, String estacionNombre, 
                        LocalDateTime fechaPrediccion, Integer horizonteHoras, 
                        Prediccion.EstadoPrediccion estado) {
        this.id = id;
        this.estacionId = estacionId;
        this.estacionNombre = estacionNombre;
        this.fechaPrediccion = fechaPrediccion;
        this.horizonteHoras = horizonteHoras;
        this.estado = estado;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEstacionId() {
        return estacionId;
    }

    public void setEstacionId(Long estacionId) {
        this.estacionId = estacionId;
    }

    public String getEstacionNombre() {
        return estacionNombre;
    }

    public void setEstacionNombre(String estacionNombre) {
        this.estacionNombre = estacionNombre;
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

    public Prediccion.EstadoPrediccion getEstado() {
        return estado;
    }

    public void setEstado(Prediccion.EstadoPrediccion estado) {
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