package com.arequipa.aire.backend.service;

import com.arequipa.aire.backend.dto.AlertaDTO;
import com.arequipa.aire.backend.entity.Alerta;
import com.arequipa.aire.backend.entity.Usuario;
import com.arequipa.aire.backend.entity.Estacion;
import com.arequipa.aire.backend.repository.AlertaRepository;
import com.arequipa.aire.backend.repository.UsuarioRepository;
import com.arequipa.aire.backend.repository.EstacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de alertas.
 */
@Service
@Transactional
public class AlertasService {

    private static final Logger logger = LoggerFactory.getLogger(AlertasService.class);

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstacionRepository estacionRepository;

    /**
     * Obtiene alertas por usuario con paginación.
     */
    public Page<AlertaDTO> obtenerAlertasPorUsuario(Long usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));

        Page<Alerta> alertas = alertaRepository.findByUsuarioOrderByFechaCreacionDesc(usuario, pageable);
        
        return alertas.map(this::convertirAAlertaDTO);
    }

    /**
     * Obtiene alertas no leídas por usuario.
     */
    public List<AlertaDTO> obtenerAlertasNoLeidas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));

        List<Alerta> alertas = alertaRepository.findByUsuarioAndLeidaFalseOrderByFechaCreacionDesc(usuario);
        
        return alertas.stream()
                .map(this::convertirAAlertaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cuenta las alertas no leídas de un usuario.
     */
    public long contarAlertasNoLeidas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));

        return alertaRepository.countByUsuarioAndLeidaFalse(usuario);
    }

    /**
     * Marca una alerta como leída.
     */
    public AlertaDTO marcarComoLeida(Long alertaId, Long usuarioId) {
        Alerta alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada: " + alertaId));

        // Verificar que la alerta pertenece al usuario
        if (!alerta.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tiene permisos para modificar esta alerta");
        }

        alerta.marcarComoLeida();
        Alerta alertaActualizada = alertaRepository.save(alerta);

        logger.info("Alerta {} marcada como leída por usuario {}", alertaId, usuarioId);

        return convertirAAlertaDTO(alertaActualizada);
    }

    /**
     * Marca todas las alertas de un usuario como leídas.
     */
    public void marcarTodasComoLeidas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));

        List<Alerta> alertasNoLeidas = alertaRepository.findByUsuarioAndLeidaFalseOrderByFechaCreacionDesc(usuario);
        
        alertasNoLeidas.forEach(alerta -> {
            alerta.marcarComoLeida();
            alertaRepository.save(alerta);
        });

        logger.info("Marcadas {} alertas como leídas para usuario {}", alertasNoLeidas.size(), usuarioId);
    }

    /**
     * Crea una nueva alerta para un usuario.
     */
    public AlertaDTO crearAlerta(Long usuarioId, Alerta.TipoAlerta tipo, Alerta.SeveridadAlerta severidad,
                                String titulo, String mensaje, Long estacionId, String contaminante,
                                Double valorMedido, Double umbralConfigurado) {
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));

        // Verificar si ya existe una alerta similar reciente (últimas 4 horas)
        LocalDateTime fechaLimite = LocalDateTime.now().minusHours(4);
        List<Alerta> alertasSimilares = alertaRepository.findAlertasSimilares(
                usuario, tipo, contaminante, fechaLimite);

        if (!alertasSimilares.isEmpty()) {
            logger.debug("Alerta similar ya existe para usuario {}, omitiendo duplicado", usuarioId);
            return convertirAAlertaDTO(alertasSimilares.get(0));
        }

        Alerta alerta = new Alerta(usuario, tipo, severidad, titulo, mensaje);
        
        if (estacionId != null) {
            Estacion estacion = estacionRepository.findById(estacionId)
                    .orElse(null);
            alerta.setEstacion(estacion);
        }
        
        alerta.setContaminante(contaminante);
        alerta.setValorMedido(valorMedido);
        alerta.setUmbralConfigurado(umbralConfigurado);

        Alerta alertaGuardada = alertaRepository.save(alerta);

        logger.info("Nueva alerta creada para usuario {}: {}", usuarioId, titulo);

        return convertirAAlertaDTO(alertaGuardada);
    }

    /**
     * Crea alerta de calidad del aire.
     */
    public AlertaDTO crearAlertaCalidadAire(Long usuarioId, String contaminante, Double valor, 
                                           Double umbral, Long estacionId, String nombreEstacion) {
        
        Alerta.SeveridadAlerta severidad = determinarSeveridad(valor, umbral);
        String titulo = String.format("Alerta de %s", contaminante);
        String mensaje = String.format("El nivel de %s en %s ha alcanzado %.1f μg/m³, " +
                                      "superando su umbral configurado de %.1f μg/m³",
                                      contaminante, nombreEstacion, valor, umbral);

        return crearAlerta(usuarioId, Alerta.TipoAlerta.CALIDAD_AIRE, severidad,
                          titulo, mensaje, estacionId, contaminante, valor, umbral);
    }

    /**
     * Elimina alertas antiguas.
     */
    @Transactional
    public void limpiarAlertasAntiguas(int diasRetencion) {
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(diasRetencion);
        alertaRepository.deleteByFechaCreacionBefore(fechaLimite);
        
        logger.info("Alertas anteriores a {} han sido eliminadas", fechaLimite);
    }

    /**
     * Determina la severidad basada en el valor y umbral.
     */
    private Alerta.SeveridadAlerta determinarSeveridad(Double valor, Double umbral) {
        if (valor == null || umbral == null) {
            return Alerta.SeveridadAlerta.MEDIA;
        }
        
        double ratio = valor / umbral;
        
        if (ratio >= 2.0) {
            return Alerta.SeveridadAlerta.CRITICA;
        } else if (ratio >= 1.5) {
            return Alerta.SeveridadAlerta.ALTA;
        } else if (ratio >= 1.1) {
            return Alerta.SeveridadAlerta.MEDIA;
        } else {
            return Alerta.SeveridadAlerta.BAJA;
        }
    }

    /**
     * Convierte una entidad Alerta a DTO.
     */
    private AlertaDTO convertirAAlertaDTO(Alerta alerta) {
        AlertaDTO dto = new AlertaDTO();
        dto.setId(alerta.getId());
        dto.setUsuarioId(alerta.getUsuario().getId());
        
        if (alerta.getEstacion() != null) {
            dto.setEstacionId(alerta.getEstacion().getId());
            dto.setEstacionNombre(alerta.getEstacion().getNombre());
        }
        
        dto.setTipo(alerta.getTipo());
        dto.setSeveridad(alerta.getSeveridad());
        dto.setTitulo(alerta.getTitulo());
        dto.setMensaje(alerta.getMensaje());
        dto.setValorMedido(alerta.getValorMedido());
        dto.setUmbralConfigurado(alerta.getUmbralConfigurado());
        dto.setContaminante(alerta.getContaminante());
        dto.setColorAlerta(alerta.getColorAlerta());
        dto.setLeida(alerta.getLeida());
        dto.setFechaLectura(alerta.getFechaLectura());
        dto.setFechaCreacion(alerta.getFechaCreacion());
        
        return dto;
    }
}