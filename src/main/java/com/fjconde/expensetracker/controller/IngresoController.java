package com.fjconde.expensetracker.controller;

import com.fjconde.expensetracker.dto.IngresoDto;
import com.fjconde.expensetracker.entity.Ingreso;
import com.fjconde.expensetracker.entity.TipoIngreso;
import com.fjconde.expensetracker.entity.Usuario;
import com.fjconde.expensetracker.service.IngresoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ingresos")
@RequiredArgsConstructor
public class IngresoController {

    private final IngresoService ingresoService;

    @GetMapping
    public String listar(@AuthenticationPrincipal Usuario usuario, Model model) {
        model.addAttribute("ingresos", ingresoService.obtenerIngresos(usuario));
        return "ingresos/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("ingresoDto", new IngresoDto());
        model.addAttribute("tipos", TipoIngreso.values());
        return "ingresos/form";
    }

    @PostMapping("/nuevo")
    public String crear(@Valid @ModelAttribute("ingresoDto") IngresoDto dto,
                        BindingResult result,
                        @AuthenticationPrincipal Usuario usuario,
                        Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tipos", TipoIngreso.values());
            return "ingresos/form";
        }
        ingresoService.crear(dto, usuario);
        return "redirect:/ingresos";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id,
                                   @AuthenticationPrincipal Usuario usuario,
                                   Model model) {
        Ingreso ingreso = ingresoService.obtenerPorId(id, usuario);

        IngresoDto dto = new IngresoDto();
        dto.setTitulo(ingreso.getTitulo());
        dto.setImporte(ingreso.getImporte());
        dto.setTipo(ingreso.getTipo());
        dto.setFecha(ingreso.getFecha());
        dto.setDescripcion(ingreso.getDescripcion());

        model.addAttribute("ingresoDto", dto);
        model.addAttribute("ingresoId", id);
        model.addAttribute("tipos", TipoIngreso.values());
        return "ingresos/form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("ingresoDto") IngresoDto dto,
                             BindingResult result,
                             @AuthenticationPrincipal Usuario usuario,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("ingresoId", id);
            model.addAttribute("tipos", TipoIngreso.values());
            return "ingresos/form";
        }
        ingresoService.actualizar(id, dto, usuario);
        return "redirect:/ingresos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id,
                           @AuthenticationPrincipal Usuario usuario) {
        ingresoService.eliminar(id, usuario);
        return "redirect:/ingresos";
    }
}
