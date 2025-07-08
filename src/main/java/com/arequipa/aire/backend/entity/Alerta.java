package com.arequipa.aire.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entidad que representa una alerta del sistema.
 */
@Entity
@Table(name = "alertas", indexes = {
    @Index(name = "idx_alerta_usuario", columnList = "usuario_id"),
    @Index(name = "idx_alerta_fecha", columnList = "fecha_creacion")
})
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacion_id")
    private Estacion estacion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAlerta tipo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeveridadAlerta severidad;

    @NotNull
    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "valor_medido")
    private Double valorMedido;

    @Column(name = "umbral_configurado")
    private Double umbralConfigurado;

    @Column(name = "contaminante", length = 10)
    private String contaminante;

    @Column(name = "color_alerta", length = 7)
    private String colorAlerta;

    @Column(nullable = false)
    private Boolean leida = false;

    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Enums
    public enum TipoAlerta {
        CALIDAD_AIRE, PREDICCION, MANTENIMIENTO, SISTEMA
    }

    public enum SeveridadAlerta {
        BAJA, MEDIA, ALTA, CRITICA
    }

    // Constructors
    public Alerta() {}

    public Alerta(Usuario usuario, TipoAlerta tipo, SeveridadAlerta severidad, String titulo, String mensaje) {
        this.usuario = usuario;
        this.tipo = tipo;
        this.severidad = severidad;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.leida = false;
        this.fechaCreacion = LocalDateTime.now();
        this.colorAlerta = getColorPorSeveridad(severidad);
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (colorAlerta == null) {
            colorAlerta = getColorPorSeveridad(severidad);
        }
    }

    private String getColorPorSeveridad(SeveridadAlerta severidad) {
        switch (severidad) {
            case BAJA: return "#28a745";      // Verde
            case MEDIA: return "#ffc107";     // Amarillo
            case ALTA: return "#fd7e14";      // Naranja
            case CRITICA: return "#dc3545";   // Rojo
            default: return "#6c757d";        // Gris
        }
    }

    public void marcarComoLeida() {
        this.leida = true;
        this.fechaLectura = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public void setEstacion(Estacion estacion) {
        this.estacion = estacion;
    }

    public TipoAlerta getTipo() {
        return tipo;
    }

    public void setTipo(TipoAlerta tipo) {
        this.tipo = tipo;
    }

    public SeveridadAlerta getSeveridad() {
        return severidad;
    }

    public void setSeveridad(SeveridadAlerta severidad) {
        this.severidad = severidad;
        this.colorAlerta = getColorPorSeveridad(severidad);
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
        if (leida && fechaLectura == null) {
            this.fechaLectura = LocalDateTime.now();
        }
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