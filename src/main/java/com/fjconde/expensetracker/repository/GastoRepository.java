package com.fjconde.expensetracker.repository;

import com.fjconde.expensetracker.entity.Gasto;
import com.fjconde.expensetracker.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Gasto.
 */
public interface GastoRepository extends JpaRepository<Gasto, Long> {

    // Gastos de un usuario ordenados por fecha descendente (más reciente primero)
    List<Gasto> findByUsuarioOrderByFechaDesc(Usuario usuario);

    // Busca un gasto por ID pero solo si pertenece al usuario — evita que un
    // usuario acceda a los gastos de otro manipulando la URL
    Optional<Gasto> findByIdAndUsuario(Long id, Usuario usuario);
}
