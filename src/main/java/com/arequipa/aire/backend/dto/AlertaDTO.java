package com.arequipa.aire.backend.dto;

import com.arequipa.aire.backend.entity.Alerta;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO para alertas.
 */
@Schema(description = "Información de alerta")
public class AlertaDTO {

    @Schema(description = "ID de la alerta", example = "1")
    private Long id;

    @Schema(description = "ID del usuario", example = "1")
    private Long usuarioId;

    @Schema(description = "ID de la estación (opcional)", example = "1")
    private Long estacionId;

    @Schema(description = "Nombre de la estación", example = "Estación Centro")
    private String estacionNombre;

    @Schema(description = "Tipo de alerta", example = "CALIDAD_AIRE")
    private Alerta.TipoAlerta tipo;

    @Schema(description = "Severidad de la alerta", example = "ALTA")
    private Alerta.SeveridadAlerta severidad;

    @Schema(description = "Título de la alerta", example = "Calidad del aire insalubre")
    private String titulo;

    @Schema(description = "Mensaje detallado de la alerta")
    private String mensaje;

    @Schema(description = "Valor medido", example = "85.5")
    private Double valorMedido;

    @Schema(description = "Umbral configurado", example = "75.0")
    private Double umbralConfigurado;

    @Schema(description = "Contaminante", example = "PM2.5")
    private String contaminante;

    @Schema(description = "Color de la alerta", example = "#ff6b6b")
    private String colorAlerta;

    @Schema(description = "Alerta leída", example = "false")
    private Boolean leida;

    @Schema(description = "Fecha de lectura")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaLectura;

    @Schema(description = "Fecha de creación")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;

    // Constructors
    public AlertaDTO() {}

    public AlertaDTO(Long id, Alerta.TipoAlerta tipo, Alerta.SeveridadAlerta severidad, 
                    String titulo, String mensaje, Boolean leida, LocalDateTime fechaCreacion) {
        this.id = id;
        this.tipo = tipo;
        this.severidad = severidad;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.leida = leida;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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

    public Alerta.TipoAlerta getTipo() {
        return tipo;
    }

    public void setTipo(Alerta.TipoAlerta tipo) {
        this.tipo = tipo;
    }

    public Alerta.SeveridadAlerta getSeveridad() {
        return severidad;
    }

    public void setSeveridad(Alerta.SeveridadAlerta severidad) {
        this.severidad = severidad;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Double getValorMedido() {
        return valorMedido;
    }

    public void setValorMedido(Double valorMedido) {
        this.valorMedido = valorMedido;
    }

    public Double getUmbralConfigurado() {
        return umbralConfigurado;
    }

    public void setUmbralConfigurado(Double umbralConfigurado) {
        this.umbralConfigurado = umbralConfigurado;
    }

    public String getContaminante() {
        return contaminante;
    }

    public void setContaminante(String contaminante) {
        this.contaminante = contaminante;
    }

    public String getColorAlerta() {
        return colorAlerta;
    }

    public void setColorAlerta(String colorAlerta) {
        this.colorAlerta = colorAlerta;
    }

    public Boolean getLeida() {
        return leida;
    }

    public void setLeida(Boolean leida) {
        this.leida = leida;
    }

    public LocalDateTime getFechaLectura() {
        return fechaLectura;
    }

    public void setFechaLectura(LocalDateTime fechaLectura) {
        this.fechaLectura = fechaLectura;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}