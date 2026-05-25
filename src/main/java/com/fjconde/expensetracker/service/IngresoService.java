package com.fjconde.expensetracker.service;

import com.fjconde.expensetracker.dto.IngresoDto;
import com.fjconde.expensetracker.entity.Ingreso;
import com.fjconde.expensetracker.entity.Usuario;
import com.fjconde.expensetracker.repository.IngresoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de ingresos. Misma estructura que GastoService.
 */
@Service
@RequiredArgsConstructor
public class IngresoService {

    private final IngresoRepository ingresoRepository;

    public List<Ingreso> obtenerIngresos(Usuario usuario) {
        return ingresoRepository.findByUsuarioOrderByFechaDesc(usuario);
    }

    public void crear(IngresoDto dto, Usuario usuario) {
        Ingreso ingreso = new Ingreso();
        mapearDtoAIngreso(dto, ingreso);
        ingreso.setUsuario(usuario);
        ingresoRepository.save(ingreso);
    }

    public Ingreso obtenerPorId(Long id, Usuario usuario) {
        return ingresoRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado"));
    }

    public void actualizar(Long id, IngresoDto dto, Usuario usuario) {
        Ingreso ingreso = obtenerPorId(id, usuario);
        mapearDtoAIngreso(dto, ingreso);
        ingresoRepository.save(ingreso);
    }

    public void eliminar(Long id, Usuario usuario) {
        Ingreso ingreso = obtenerPorId(id, usuario);
        ingresoRepository.delete(ingreso);
    }

    private void mapearDtoAIngreso(IngresoDto dto, Ingreso ingreso) {
        ingreso.setTitulo(dto.getTitulo());
        ingreso.setImporte(dto.getImporte());
        ingreso.setTipo(dto.getTipo());
        ingreso.setFecha(dto.getFecha());
        ingreso.setDescripcion(dto.getDescripcion());
    }
}
