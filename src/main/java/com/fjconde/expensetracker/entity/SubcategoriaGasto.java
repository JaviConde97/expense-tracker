package com.fjconde.expensetracker.entity;

/**
 * Subcategorías de gasto.
 * Cada subcategoría pertenece a una categoría principal.
 * El campo 'categoria' permite filtrarlas en el formulario mediante JavaScript.
 */
public enum SubcategoriaGasto {

    // Vivienda
    HIPOTECA_ALQUILER("Hipoteca o alquiler",         CategoriaGasto.VIVIENDA),
    TELEFONO("Teléfono",                             CategoriaGasto.VIVIENDA),
    ELECTRICIDAD("Electricidad",                     CategoriaGasto.VIVIENDA),
    GAS("Gas",                                       CategoriaGasto.VIVIENDA),
    AGUA("Agua y alcantarillado",                    CategoriaGasto.VIVIENDA),
    CABLE("Televisión por cable",                    CategoriaGasto.VIVIENDA),
    RESIDUOS("Recogida de residuos",                 CategoriaGasto.VIVIENDA),
    MANTENIMIENTO("Mantenimiento o reparaciones",    CategoriaGasto.VIVIENDA),
    SUMINISTROS("Suministros",                       CategoriaGasto.VIVIENDA),
    VIVIENDA_OTROS("Otros",                          CategoriaGasto.VIVIENDA),

    // Transporte
    PAGO_VEHICULO("Pago del vehículo",               CategoriaGasto.TRANSPORTE),
    TAXI_BUS("Taxi o bus",                           CategoriaGasto.TRANSPORTE),
    SEGURO_VEHICULO("Seguro",                        CategoriaGasto.TRANSPORTE),
    COMBUSTIBLE("Combustible",                       CategoriaGasto.TRANSPORTE),
    MANTENIMIENTO_VEHICULO("Mantenimiento",          CategoriaGasto.TRANSPORTE),
    REPARACION_VEHICULO("Reparación",                CategoriaGasto.TRANSPORTE),
    TRANSPORTE_OTROS("Otros",                        CategoriaGasto.TRANSPORTE),

    // Comida
    ALIMENTOS("Alimentos",                           CategoriaGasto.COMIDA),
    RESTAURANTES("Restaurantes",                     CategoriaGasto.COMIDA),
    APLICACION_COMIDA("Aplicación de comida",        CategoriaGasto.COMIDA),

    // Cuidado personal
    PELO("Pelo",                                     CategoriaGasto.CUIDADO_PERSONAL),
    ROPA("Ropa",                                     CategoriaGasto.CUIDADO_PERSONAL),
    GIMNASIO("Gimnasio",                             CategoriaGasto.CUIDADO_PERSONAL),
    CUIDADO_OTROS("Otros",                           CategoriaGasto.CUIDADO_PERSONAL),

    // Entretenimiento
    QUEDADAS("Quedadas",                             CategoriaGasto.ENTRETENIMIENTO),
    COMPRAS("Compras",                               CategoriaGasto.ENTRETENIMIENTO),
    JUEGOS("Juegos",                                 CategoriaGasto.ENTRETENIMIENTO),
    LIBROS("Libros",                                 CategoriaGasto.ENTRETENIMIENTO),
    SUSCRIPCIONES("Suscripciones",                   CategoriaGasto.ENTRETENIMIENTO),
    ENTRETENIMIENTO_OTROS("Otros",                   CategoriaGasto.ENTRETENIMIENTO),

    // Inversiones
    CUENTA_AHORRO("Cuenta de ahorro",                CategoriaGasto.INVERSIONES),
    CUENTA_INVERSION("Cuenta de inversión",          CategoriaGasto.INVERSIONES),
    INVERSIONES_OTROS("Otros",                       CategoriaGasto.INVERSIONES);

    private final String nombre;
    private final CategoriaGasto categoria;

    SubcategoriaGasto(String nombre, CategoriaGasto categoria) {
        this.nombre = nombre;
        this.categoria = categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public CategoriaGasto getCategoria() {
        return categoria;
    }
}
