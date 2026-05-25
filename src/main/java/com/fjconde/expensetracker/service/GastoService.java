package com.fjconde.expensetracker.service;

import com.fjconde.expensetracker.dto.GastoDto;
import com.fjconde.expensetracker.entity.Gasto;
import com.fjconde.expensetracker.entity.Usuario;
import com.fjconde.expensetracker.repository.GastoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de gastos.
 * Todas las operaciones validan que el gasto pertenezca al usuario autenticado.
 */
@Service
@RequiredArgsConstructor
public class GastoService {

    private final GastoRepository gastoRepository;

    /**
     * Devuelve todos los gastos del usuario ordenados por fecha descendente.
     */
    public List<Gasto> obtenerGastos(Usuario usuario) {
        return gastoRepository.findByUsuarioOrderByFechaDesc(usuario);
    }

    /**
     * Crea un nuevo gasto asociado al usuario autenticado.
     */
    public void crear(GastoDto dto, Usuario usuario) {
        Gasto gasto = new Gasto();
        mapearDtoAGasto(dto, gasto);
        gasto.setUsuario(usuario);
        gastoRepository.save(gasto);
    }

    /**
     * Busca un gasto por ID verificando que pertenezca al usuario.
     * Lanza excepción si no existe o si pertenece a otro usuario.
     */
    public Gasto obtenerPorId(Long id, Usuario usuario) {
        return gastoRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new IllegalArgumentException("Gasto no encontrado"));
    }

    /**
     * Actualiza los datos de un gasto existente.
     */
    public void actualizar(Long id, GastoDto dto, Usuario usuario) {
        Gasto gasto = obtenerPorId(id, usuario);
        mapearDtoAGasto(dto, gasto);
        gastoRepository.save(gasto);
    }

    /**
     * Elimina un gasto verificando que pertenezca al usuario.
     */
    public void eliminar(Long id, Usuario usuario) {
        Gasto gasto = obtenerPorId(id, usuario);
        gastoRepository.delete(gasto);
    }

    /**
     * Copia los datos del DTO a la entidad Gasto.
     * Usado tanto en crear como en actualizar.
     */
    private void mapearDtoAGasto(GastoDto dto, Gasto gasto) {
        gasto.setTitulo(dto.getTitulo());
        gasto.setImporte(dto.getImporte());
        // La categoría se deriva automáticamente de la subcategoría (el enum ya la tiene)
        gasto.setSubcategoria(dto.getSubcategoria());
        gasto.setCategoria(dto.getSubcategoria().getCategoria());
        gasto.setFecha(dto.getFecha());
        gasto.setDescripcion(dto.getDescripcion());
    }
}
