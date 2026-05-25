package com.fjconde.expensetracker.entity;

/**
 * Categorías principales de gasto.
 * Cada categoría tiene un nombre para mostrar en la interfaz.
 */
public enum CategoriaGasto {

    VIVIENDA("Vivienda"),
    TRANSPORTE("Transporte"),
    COMIDA("Comida"),
    CUIDADO_PERSONAL("Cuidado personal"),
    ENTRETENIMIENTO("Entretenimiento"),
    INVERSIONES("Inversiones");

    private final String nombre;

    CategoriaGasto(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
