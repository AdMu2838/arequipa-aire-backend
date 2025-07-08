package com.arequipa.aire.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO para datos de calidad del aire.
 */
@Schema(description = "Datos de calidad del aire")
public class CalidadAireDTO {

    @Schema(description = "ID de la estación", example = "1")
    private Long estacionId;

    @Schema(description = "Nombre de la estación", example = "Estación Centro")
    private String estacionNombre;

    @Schema(description = "Distrito", example = "Cercado")
    private String distrito;

    @Schema(description = "Latitud", example = "-16.4090")
    private Double latitud;

    @Schema(description = "Longitud", example = "-71.5375")
    private Double longitud;

    @Schema(description = "Fecha y hora de la medición")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaMedicion;

    // Contaminantes
    @Schema(description = "Partículas PM2.5 (μg/m³)", example = "35.5")
    private Double pm25;

    @Schema(description = "Partículas PM10 (μg/m³)", example = "45.2")
    private Double pm10;

    @Schema(description = "Dióxido de nitrógeno NO₂ (μg/m³)", example = "25.1")
    private Double no2;

    @Schema(description = "Ozono O₃ (μg/m³)", example = "80.3")
    private Double o3;

    @Schema(description = "Monóxido de carbono CO (μg/m³)", example = "1200.5")
    private Double co;

    @Schema(description = "Dióxido de azufre SO₂ (μg/m³)", example = "15.2")
    private Double so2;

    // AQI
    @Schema(description = "Índice de Calidad del Aire", example = "85")
    private Integer aqi;

    @Schema(description = "Categoría AQI", example = "Moderada")
    private String categoriaAqi;

    @Schema(description = "Color asociado al AQI", example = "#FFFF00")
    private String colorAqi;

    @Schema(description = "Recomendación de salud", example = "Grupos sensibles deben considerar limitar actividades al aire libre")
    private String recomendacion;

    // Datos meteorológicos
    @Schema(description = "Temperatura (°C)", example = "22.5")
    private Double temperatura;

    @Schema(description = "Humedad (%)", example = "65")
    private Integer humedad;

    @Schema(description = "Presión atmosférica (hPa)", example = "1013.25")
    private Double presion;

    @Schema(description = "Velocidad del viento (km/h)", example = "8.5")
    private Double velocidadViento;

    @Schema(description = "Dirección del viento (grados)", example = "230")
    private Integer direccionViento;

    // Metadatos
    @Schema(description = "Fuente de los datos", example = "OpenWeatherMap")
    private String fuenteDatos;

    @Schema(description = "Confiabilidad de los datos (0-1)", example = "0.95")
    private Double confiabilidad;

    // Constructors
    public CalidadAireDTO() {}

    // Getters and Setters
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

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
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

    public String getRecomendacion() {
        return recomendacion;
    }

    public void setRecomendacion(String recomendacion) {
        this.recomendacion = recomendacion;
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
}