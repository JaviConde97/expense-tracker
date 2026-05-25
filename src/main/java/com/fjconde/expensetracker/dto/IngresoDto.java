package com.fjconde.expensetracker.dto;

import com.fjconde.expensetracker.entity.TipoIngreso;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class IngresoDto {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotNull(message = "El importe es obligatorio")
    @DecimalMin(value = "0.01", message = "El importe debe ser mayor que 0")
    private BigDecimal importe;

    @NotNull(message = "El tipo es obligatorio")
    private TipoIngreso tipo;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    private String descripcion;
}
