package com.fjconde.expensetracker.entity;

/**
 * Tipos de ingreso disponibles.
 */
public enum TipoIngreso {

    NOMINA("Nómina"),
    FREELANCE("Freelance"),
    INMUEBLES("Inmuebles"),
    INTERESES("Intereses"),
    DIVIDENDOS("Dividendos"),
    VENTA_ACTIVOS("Venta de activos"),
    PRESTACIONES("Prestaciones"),
    OTROS("Otros");

    private final String nombre;

    TipoIngreso(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
