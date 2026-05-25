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
import org.springframework.web.bind.annotation.RequestParam;

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
    public String dashboard(@AuthenticationPrincipal Usuario usuario,
                            @RequestParam(required = false) Integer mes,
                            @RequestParam(required = false) Integer anio,
                            Model model) {

        LocalDate hoy = LocalDate.now();
        int mesActual  = (mes  != null) ? mes  : hoy.getMonthValue();
        int anioActual = (anio != null) ? anio : hoy.getYear();

        // Mes anterior y siguiente para los botones de navegación
        LocalDate fechaActual = LocalDate.of(anioActual, mesActual, 1);
        LocalDate fechaAnterior = fechaActual.minusMonths(1);
        LocalDate fechaSiguiente = fechaActual.plusMonths(1);

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

        // --- Últimos 5 gastos del mes seleccionado ---
        List<Gasto> ultimosGastos = gastosDelMes.stream()
                .limit(5)
                .collect(Collectors.toList());

        // Nombre del mes actual en español para el título del dashboard
        String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio",
                          "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
        String[] mesesCortos = {"Ene","Feb","Mar","Abr","May","Jun",
                                "Jul","Ago","Sep","Oct","Nov","Dic"};
        String nombreMes = meses[mesActual - 1];

        // --- Gráfico anual: gastos e ingresos por mes del año actual ---
        List<Gasto>   todosLosGastos   = gastoService.obtenerGastos(usuario);
        List<Ingreso> todosLosIngresos = ingresoService.obtenerIngresos(usuario);

        List<String>     barLabels   = new ArrayList<>();
        List<BigDecimal> barGastos   = new ArrayList<>();
        List<BigDecimal> barIngresos = new ArrayList<>();

        for (int numMes = 1; numMes <= 12; numMes++) {
            final int m = numMes;

            BigDecimal gastosMes = todosLosGastos.stream()
                    .filter(g -> g.getFecha().getYear() == anioActual && g.getFecha().getMonthValue() == m)
                    .map(Gasto::getImporte)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal ingresosMes = todosLosIngresos.stream()
                    .filter(i -> i.getFecha().getYear() == anioActual && i.getFecha().getMonthValue() == m)
                    .map(Ingreso::getImporte)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            barLabels.add(mesesCortos[numMes - 1]);
            barGastos.add(gastosMes);
            barIngresos.add(ingresosMes);
        }

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
        model.addAttribute("barLabels",      barLabels);
        model.addAttribute("barGastos",      barGastos);
        model.addAttribute("barIngresos",    barIngresos);
        model.addAttribute("anioActual",     anioActual);
        model.addAttribute("mesActual",      mesActual);
        model.addAttribute("mesAnterior",    fechaAnterior.getMonthValue());
        model.addAttribute("anioAnterior",   fechaAnterior.getYear());
        model.addAttribute("mesSiguiente",   fechaSiguiente.getMonthValue());
        model.addAttribute("anioSiguiente",  fechaSiguiente.getYear());

        return "dashboard";
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/dashboard";
    }
}
