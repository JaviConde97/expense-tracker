package com.fjconde.expensetracker.dto;

import com.fjconde.expensetracker.entity.CategoriaGasto;
import com.fjconde.expensetracker.entity.SubcategoriaGasto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para el formulario de creación y edición de gastos.
 */
@Getter
@Setter
public class GastoDto {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotNull(message = "El importe es obligatorio")
    @DecimalMin(value = "0.01", message = "El importe debe ser mayor que 0")
    private BigDecimal importe;

    @NotNull(message = "La categoría es obligatoria")
    private CategoriaGasto categoria;

    @NotNull(message = "La subcategoría es obligatoria")
    private SubcategoriaGasto subcategoria;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    private String descripcion;
}
