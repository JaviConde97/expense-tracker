package com.fjconde.expensetracker.repository;

import com.fjconde.expensetracker.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Usuario.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Spring Data genera automáticamente la query: SELECT * FROM usuarios WHERE email = ?
    Optional<Usuario> findByEmail(String email);

    // Para comprobar si ya existe un usuario con ese email antes de registrar
    boolean existsByEmail(String email);
}
