# Expense Tracker

Aplicación web para gestión de gastos e ingresos personales con dashboard de estadísticas mensuales.

---

## Stack

Java 21 · Spring Boot 3.5 · Spring Security · Thymeleaf · Bootstrap 5 · JavaScript · Chart.js · Tom Select · JPA/Hibernate · H2 (dev) · MySQL (prod) · Maven

---

## Funcionalidades

- Registro e inicio de sesión con sesión HTTP ✅
- CRUD completo de gastos (título, importe, categoría, subcategoría, fecha, descripción) ✅
- 6 categorías con 30 subcategorías (Vivienda, Transporte, Comida, Cuidado personal, Entretenimiento, Inversiones) ✅
- CRUD completo de ingresos con 8 tipos (Nómina, Freelance, Inmuebles, Intereses, Dividendos, Venta de activos, Prestaciones, Otros) ✅
- Dashboard mensual con ingresos, gastos, balance y número de transacciones ✅
- Navegación entre meses en el dashboard con botones anterior/siguiente y botón "Hoy" ✅
- Gráfica de donut por categoría de gasto del mes seleccionado ✅
- Gráfica de barras anual comparando gastos e ingresos mes a mes ✅
- Gastos del mes seleccionado en el dashboard ✅
- Lista de gastos con columna `Categoría › Subcategoría` y total al pie de tabla ✅
- Lista de ingresos con total al pie de tabla ✅
- Formulario de gasto con selector buscable agrupado por categoría (Tom Select) ✅
- Aislamiento de datos: cada usuario solo ve sus propios gastos e ingresos ✅
- Validación de formularios ✅

---

## Estructura

```
src/main/java/com/fjconde/expensetracker/
├── config/
│   ├── AppConfig.java              → PasswordEncoder (separado para evitar ciclo)
│   └── SecurityConfig.java         → Spring Security con sesión HTTP
├── controller/
│   ├── AuthController.java         → login y registro
│   ├── DashboardController.java    → estadísticas del mes
│   ├── GastoController.java        → CRUD de gastos
│   └── IngresoController.java      → CRUD de ingresos
├── dto/
│   ├── GastoDto.java
│   ├── IngresoDto.java
│   └── RegistroDto.java
├── entity/
│   ├── CategoriaGasto.java         → enum con 6 categorías
│   ├── Gasto.java
│   ├── Ingreso.java
│   ├── SubcategoriaGasto.java      → enum con 30 subcategorías
│   ├── TipoIngreso.java            → enum con 8 tipos de ingreso
│   └── Usuario.java
├── repository/
│   ├── GastoRepository.java
│   ├── IngresoRepository.java
│   └── UsuarioRepository.java
└── service/
    ├── GastoService.java
    ├── IngresoService.java
    └── UsuarioService.java

src/main/resources/
├── templates/
│   ├── auth/
│   │   ├── login.html
│   │   └── registro.html
│   ├── fragments/
│   │   └── navbar.html
│   ├── gastos/
│   │   ├── form.html               → crear y editar (mismo formulario)
│   │   └── lista.html
│   ├── ingresos/
│   │   ├── form.html
│   │   └── lista.html
│   └── dashboard.html
├── application.properties
├── application-dev.properties      → H2
└── application-prod.properties     → MySQL
```

---

## Uso

### 1. Registrarse

Ir a `http://localhost:8080/auth/registro` e introducir nombre, email y contraseña.

> En modo desarrollo (H2) la base de datos es en memoria: se vacía al reiniciar la app, por lo que hay que registrarse de nuevo en cada arranque.

### 2. Iniciar sesión

Usar el email y contraseña del registro en `http://localhost:8080/auth/login`.

### 3. Registrar ingresos

Desde "Ingresos" en la navbar puedes crear, editar y eliminar ingresos:

- Selecciona el **tipo** de ingreso: Nómina, Freelance, Inmuebles, Intereses, Dividendos, Venta de activos, Prestaciones u Otros
- Introduce el **importe** en euros
- Elige la **fecha** del ingreso
- La **descripción** es opcional

### 4. Gestionar gastos

Desde "Gastos" puedes crear, editar y eliminar gastos:

- En el campo **¿En qué gastaste?** escribe para buscar directamente o despliega para ver todas las opciones agrupadas por categoría (Vivienda, Transporte, Comida...)
- Introduce el **importe** en euros
- Elige la **fecha** del gasto
- La **descripción** es opcional

La lista de gastos muestra la columna categoría en formato `Vivienda › Hipoteca o alquiler` y el total acumulado al pie de la tabla.

### 5. Consultar el dashboard

El dashboard muestra el resumen del mes seleccionado:

- **Ingresos del mes** — suma de todos los ingresos del mes
- **Gastos del mes** — suma de todos los gastos del mes
- **Balance** — ingresos menos gastos (verde si positivo, rojo si negativo)
- **Gráfica de donut** — distribución de gastos por categoría del mes
- **Gastos del mes** — los 5 más recientes del mes seleccionado
- **Gráfica de barras anual** — comparativa de gastos e ingresos mes a mes del año actual

Usa los botones **←** y **→** para navegar entre meses o **Hoy** para volver al mes actual.

---

## Ejecución

### Local (modo desarrollo con H2)

**Requisitos:** Java 21+, Maven 3.8+

```bash
git clone https://github.com/JaviConde97/expense-tracker.git
cd expense-tracker
./mvnw spring-boot:run
```

La aplicación arranca en `http://localhost:8080`.

### Con Docker (modo producción con MySQL)

**Requisitos:** Docker y Docker Compose

```bash
git clone https://github.com/JaviConde97/expense-tracker.git
cd expense-tracker
docker-compose up --build
```

---

## Consola H2

Disponible en `http://localhost:8080/h2-console` (solo en modo desarrollo).

- JDBC URL: `jdbc:h2:mem:expensetracker`
- Usuario: `sa` · Contraseña: *(vacía)*

---

## Documentación técnica

Decisiones de arquitectura, errores encontrados y explicaciones del código en [`docs/desarrollo.md`](docs/desarrollo.md).
