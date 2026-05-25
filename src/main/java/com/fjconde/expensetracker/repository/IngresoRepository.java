package com.fjconde.expensetracker.repository;

import com.fjconde.expensetracker.entity.Ingreso;
import com.fjconde.expensetracker.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IngresoRepository extends JpaRepository<Ingreso, Long> {

    List<Ingreso> findByUsuarioOrderByFechaDesc(Usuario usuario);

    Optional<Ingreso> findByIdAndUsuario(Long id, Usuario usuario);
}
