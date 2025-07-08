package com.arequipa.aire.backend.repository;

import com.arequipa.aire.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Encuentra un usuario por nombre de usuario.
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Encuentra un usuario por email.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Encuentra un usuario por username o email.
     */
    Optional<Usuario> findByUsernameOrEmail(String username, String email);

    /**
     * Verifica si existe un usuario con el username dado.
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con el email dado.
     */
    boolean existsByEmail(String email);

    /**
     * Encuentra usuarios activos por rol.
     */
    List<Usuario> findByRoleAndActivoTrue(Usuario.Role role);

    /**
     * Encuentra usuarios por tipo de sensibilidad.
     */
    List<Usuario> findByTipoSensibilidadAndActivoTrue(Usuario.TipoSensibilidad tipoSensibilidad);

    /**
     * Encuentra usuarios activos.
     */
    List<Usuario> findByActivoTrue();

    /**
     * Cuenta usuarios por rol.
     */
    long countByRole(Usuario.Role role);

    /**
     * Encuentra usuarios inactivos desde una fecha específica.
     */
    @Query("SELECT u FROM Usuario u WHERE u.fechaUltimoAcceso < :fechaLimite OR u.fechaUltimoAcceso IS NULL")
    List<Usuario> findUsuariosInactivos(LocalDateTime fechaLimite);

    /**
     * Encuentra usuarios registrados en un período específico.
     */
    List<Usuario> findByFechaCreacionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Busca usuarios por nombre completo (búsqueda parcial).
     */
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :nombre, '%')) AND u.activo = true")
    List<Usuario> findByNombreCompletoContainingIgnoreCase(String nombre);
}