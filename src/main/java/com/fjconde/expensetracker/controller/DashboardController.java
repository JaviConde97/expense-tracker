package com.fjconde.expensetracker.controller;

import com.fjconde.expensetracker.entity.CategoriaGasto;
import com.fjconde.expensetracker.entity.Gasto;
import com.fjconde.expensetracker.entity.Ingreso;
import com.fjconde.expensetracker.entity.Usuario;
import com.fjconde.expensetracker.service.GastoService;
import com.fjconde.expensetracker.service.IngresoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador del dashboard principal.
 * Calcula las estadísticas del mes actual y las pasa a la vista.
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final GastoService gastoService;
    private final IngresoService ingresoService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal Usuario usuario, Model model) {

        LocalDate hoy = LocalDate.now();
        int mesActual  = hoy.getMonthValue();
        int anioActual = hoy.getYear();

        // --- Gastos del mes actual ---
        List<Gasto> gastosDelMes = gastoService.obtenerGastos(usuario).stream()
                .filter(g -> g.getFecha().getMonthValue() == mesActual
                          && g.getFecha().getYear() == anioActual)
                .collect(Collectors.toList());

        BigDecimal totalGastos = gastosDelMes.stream()
                .map(Gasto::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // --- Ingresos del mes actual ---
        List<Ingreso> ingresosDelMes = ingresoService.obtenerIngresos(usuario).stream()
                .filter(i -> i.getFecha().getMonthValue() == mesActual
                          && i.getFecha().getYear() == anioActual)
                .collect(Collectors.toList());

        BigDecimal totalIngresos = ingresosDelMes.stream()
                .map(Ingreso::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Balance = ingresos - gastos
        BigDecimal balance = totalIngresos.subtract(totalGastos);

        // --- Gasto más alto del mes ---
        BigDecimal gastoMasAlto = gastosDelMes.stream()
                .map(Gasto::getImporte)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // --- Gastos por categoría (para la gráfica Chart.js) ---
        // Necesitamos dos listas paralelas: etiquetas y valores
        Map<CategoriaGasto, BigDecimal> gastosPorCategoria = gastosDelMes.stream()
                .collect(Collectors.groupingBy(
                        Gasto::getCategoria,
                        Collectors.reducing(BigDecimal.ZERO, Gasto::getImporte, BigDecimal::add)
                ));

        // Ordenamos por importe descendente para que la gráfica quede más clara
        List<Map.Entry<CategoriaGasto, BigDecimal>> entradasOrdenadas = gastosPorCategoria.entrySet()
                .stream()
                .sorted(Map.Entry.<CategoriaGasto, BigDecimal>comparingByValue().reversed())
                .collect(Collectors.toList());

        List<String> chartLabels = entradasOrdenadas.stream()
                .map(e -> e.getKey().getNombre())
                .collect(Collectors.toList());

        List<BigDecimal> chartData = entradasOrdenadas.stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        // --- Últimos 5 gastos (de todos los meses, no solo el actual) ---
        List<Gasto> ultimosGastos = gastoService.obtenerGastos(usuario).stream()
                .limit(5)
                .collect(Collectors.toList());

        // Nombre del mes actual en español para el título del dashboard
        String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio",
                          "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
        String nombreMes = meses[mesActual - 1];

        // Pasamos todo al modelo
        model.addAttribute("nombreMes",      nombreMes);
        model.addAttribute("totalGastos",    totalGastos);
        model.addAttribute("totalIngresos",  totalIngresos);
        model.addAttribute("balance",        balance);
        model.addAttribute("gastoMasAlto",   gastoMasAlto);
        model.addAttribute("numGastos",      gastosDelMes.size());
        model.addAttribute("chartLabels",    chartLabels);
        model.addAttribute("chartData",      chartData);
        model.addAttribute("ultimosGastos",  ultimosGastos);

        return "dashboard";
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/dashboard";
    }
}
