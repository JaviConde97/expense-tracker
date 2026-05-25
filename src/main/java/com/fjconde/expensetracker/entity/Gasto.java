package com.fjconde.expensetracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que representa un gasto del usuario.
 * Cada gasto pertenece a un único usuario (relación ManyToOne).
 */
@Entity
@Table(name = "gastos")
@Getter
@Setter
@NoArgsConstructor
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    // BigDecimal para dinero — nunca usar float/double (problemas de precisión)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal importe;

    // Guardamos el enum como String en la BD (más legible que el número ordinal)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaGasto categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubcategoriaGasto subcategoria;

    @Column(nullable = false)
    private LocalDate fecha;

    // Descripción opcional
    private String descripcion;

    // Relación con el usuario propietario del gasto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
