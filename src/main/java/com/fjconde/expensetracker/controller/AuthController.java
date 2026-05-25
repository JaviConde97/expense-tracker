package com.fjconde.expensetracker.controller;

import com.fjconde.expensetracker.dto.RegistroDto;
import com.fjconde.expensetracker.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador de autenticación: login y registro.
 *
 * Nota: el procesamiento del login (POST /auth/login) lo gestiona
 * Spring Security automáticamente — aquí solo manejamos el GET
 * para mostrar el formulario.
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    /**
     * Muestra el formulario de login.
     * Los parámetros error y logout los añade Spring Security a la URL.
     */
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("errorMsg", "Email o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("logoutMsg", "Has cerrado sesión correctamente");
        }
        return "auth/login";
    }

    /**
     * Muestra el formulario de registro.
     */
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("registroDto", new RegistroDto());
        return "auth/registro";
    }

    /**
     * Procesa el formulario de registro.
     * Si hay errores de validación, vuelve a mostrar el formulario.
     * Si el email ya existe, muestra un error.
     * Si todo va bien, redirige al login.
     */
    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute("registroDto") RegistroDto dto,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors()) {
            return "auth/registro";
        }

        try {
            usuarioService.registrar(dto);
            return "redirect:/auth/login?registro=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "auth/registro";
        }
    }
}
