# Expense Tracker

Aplicación web para gestión de gastos personales.
Cada usuario registra y visualiza sus propios gastos organizados por categoría y subcategoría.

---

## Stack

Java 21 · Spring Boot 3.5 · Spring Security · Thymeleaf · Bootstrap 5 · JavaScript · JPA/Hibernate · H2 (dev) · MySQL (prod) · Maven

---

## Funcionalidades

- Registro e inicio de sesión con sesión HTTP ✅
- CRUD completo de gastos (título, importe, categoría, subcategoría, fecha, descripción) ✅
- 6 categorías con 30 subcategorías (Vivienda, Transporte, Comida, Cuidado personal, Entretenimiento, Inversiones) ✅
- Formulario con dropdown de subcategorías dependiente de la categoría seleccionada ✅
- Aislamiento de datos: cada usuario solo ve sus propios gastos ✅
- Validación de formularios ✅
- Dashboard con total gastado y desglose por categoría
- Filtro por mes

---

## Estructura

```
src/main/java/com/fjconde/expensetracker/
├── config/
│   ├── AppConfig.java          → PasswordEncoder (separado para evitar ciclo)
│   └── SecurityConfig.java     → Spring Security con sesión HTTP
├── controller/
│   ├── AuthController.java     → login y registro
│   ├── DashboardController.java
│   └── GastoController.java    → CRUD de gastos
├── dto/
│   ├── GastoDto.java
│   └── RegistroDto.java
├── entity/
│   ├── CategoriaGasto.java     → enum con 6 categorías
│   ├── Gasto.java
│   ├── SubcategoriaGasto.java  → enum con 30 subcategorías
│   └── Usuario.java
├── repository/
│   ├── GastoRepository.java
│   └── UsuarioRepository.java
└── service/
    ├── GastoService.java
    └── UsuarioService.java

src/main/resources/
├── templates/
│   ├── auth/
│   │   ├── login.html
│   │   └── registro.html
│   ├── fragments/
│   │   └── navbar.html
│   ├── gastos/
│   │   ├── form.html           → crear y editar (mismo formulario)
│   │   └── lista.html
│   └── dashboard.html
├── application.properties
├── application-dev.properties  → H2
└── application-prod.properties → MySQL
```

---

## Uso

### 1. Registrarse

Ir a `http://localhost:8080/auth/registro` e introducir nombre, email y contraseña.

> En modo desarrollo (H2) la base de datos es en memoria: se vacía al reiniciar la app, por lo que hay que registrarse de nuevo en cada arranque.

### 2. Iniciar sesión

Usar el email y contraseña del registro en `http://localhost:8080/auth/login`.

### 3. Gestionar gastos

Desde "Mis gastos" puedes crear, editar y eliminar gastos. Al crear un gasto:

- Selecciona una **categoría** — el segundo desplegable se actualiza automáticamente con sus subcategorías
- Introduce el **importe** en euros
- Elige la **fecha** del gasto
- La **descripción** es opcional

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
