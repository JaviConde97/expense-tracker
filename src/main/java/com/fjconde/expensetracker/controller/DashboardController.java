package com.fjconde.expensetracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador del dashboard principal.
 * Se ampliará en la Fase 4 con estadísticas de gastos.
 */
@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    // Redirige la raíz al dashboard
    @GetMapping("/")
    public String raiz() {
        return "redirect:/dashboard";
    }
}
