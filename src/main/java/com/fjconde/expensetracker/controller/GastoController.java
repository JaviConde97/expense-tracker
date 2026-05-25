package com.fjconde.expensetracker.controller;

import com.fjconde.expensetracker.dto.GastoDto;
import com.fjconde.expensetracker.entity.CategoriaGasto;
import com.fjconde.expensetracker.entity.Gasto;
import com.fjconde.expensetracker.entity.SubcategoriaGasto;
import com.fjconde.expensetracker.entity.Usuario;
import com.fjconde.expensetracker.service.GastoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador CRUD de gastos.
 * Usa @AuthenticationPrincipal para obtener el usuario autenticado directamente
 * sin tener que hacer una consulta extra a la base de datos.
 */
@Controller
@RequestMapping("/gastos")
@RequiredArgsConstructor
public class GastoController {

    private final GastoService gastoService;

    // -------------------------------------------------------------------------
    // Listado
    // -------------------------------------------------------------------------

    @GetMapping
    public String listar(@AuthenticationPrincipal Usuario usuario, Model model) {
        model.addAttribute("gastos", gastoService.obtenerGastos(usuario));
        return "gastos/lista";
    }

    // -------------------------------------------------------------------------
    // Crear
    // -------------------------------------------------------------------------

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("gastoDto", new GastoDto());
        agregarDatosFormulario(model);
        return "gastos/form";
    }

    @PostMapping("/nuevo")
    public String crear(@Valid @ModelAttribute("gastoDto") GastoDto dto,
                        BindingResult result,
                        @AuthenticationPrincipal Usuario usuario,
                        Model model) {
        if (result.hasErrors()) {
            agregarDatosFormulario(model);
            return "gastos/form";
        }
        gastoService.crear(dto, usuario);
        return "redirect:/gastos";
    }

    // -------------------------------------------------------------------------
    // Editar
    // -------------------------------------------------------------------------

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id,
                                   @AuthenticationPrincipal Usuario usuario,
                                   Model model) {
        Gasto gasto = gastoService.obtenerPorId(id, usuario);

        // Convertimos la entidad a DTO para pre-rellenar el formulario
        GastoDto dto = new GastoDto();
        dto.setTitulo(gasto.getTitulo());
        dto.setImporte(gasto.getImporte());
        dto.setCategoria(gasto.getCategoria());
        dto.setSubcategoria(gasto.getSubcategoria());
        dto.setFecha(gasto.getFecha());
        dto.setDescripcion(gasto.getDescripcion());

        model.addAttribute("gastoDto", dto);
        model.addAttribute("gastoId", id);
        agregarDatosFormulario(model);
        return "gastos/form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("gastoDto") GastoDto dto,
                             BindingResult result,
                             @AuthenticationPrincipal Usuario usuario,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("gastoId", id);
            agregarDatosFormulario(model);
            return "gastos/form";
        }
        gastoService.actualizar(id, dto, usuario);
        return "redirect:/gastos";
    }

    // -------------------------------------------------------------------------
    // Eliminar
    // -------------------------------------------------------------------------

    // Los formularios HTML solo soportan GET y POST, no DELETE.
    // Usamos POST con la URL /eliminar para simular el borrado.
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id,
                           @AuthenticationPrincipal Usuario usuario) {
        gastoService.eliminar(id, usuario);
        return "redirect:/gastos";
    }

    // -------------------------------------------------------------------------
    // Helper privado
    // -------------------------------------------------------------------------

    /**
     * Añade al modelo los datos necesarios para el formulario:
     * - Lista de categorías para el primer select
     * - Mapa categoría → subcategorías para el JS del dropdown dependiente
     */
    private void agregarDatosFormulario(Model model) {
        model.addAttribute("categorias", CategoriaGasto.values());

        // Construimos el mapa que usará JavaScript para filtrar subcategorías
        // Estructura: { "VIVIENDA": [{value:"HIPOTECA_ALQUILER", label:"Hipoteca o alquiler"}, ...], ... }
        Map<String, List<Map<String, String>>> subcategoriasPorCategoria = new LinkedHashMap<>();
        for (CategoriaGasto cat : CategoriaGasto.values()) {
            List<Map<String, String>> subs = Arrays.stream(SubcategoriaGasto.values())
                    .filter(s -> s.getCategoria() == cat)
                    .map(s -> {
                        Map<String, String> entry = new LinkedHashMap<>();
                        entry.put("value", s.name());
                        entry.put("label", s.getNombre());
                        return entry;
                    })
                    .collect(Collectors.toList());
            subcategoriasPorCategoria.put(cat.name(), subs);
        }
        model.addAttribute("subcategoriasPorCategoria", subcategoriasPorCategoria);
    }
}
