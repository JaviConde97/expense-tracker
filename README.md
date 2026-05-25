# Expense Tracker

Aplicación web para gestión de gastos personales.
Cada usuario registra y visualiza sus propios gastos con un dashboard de resumen.

---

## Stack

Java 21 · Spring Boot 3.5 · Spring Security · Thymeleaf · Bootstrap 5 · JPA/Hibernate · H2 (dev) · MySQL (prod) · Maven

---

## Funcionalidades

- Registro e inicio de sesión con sesión HTTP ✅
- CRUD completo de gastos (título, importe, categoría, fecha)
- Dashboard con total gastado y desglose por categoría
- Filtro por mes
- Aislamiento de datos: cada usuario solo ve sus propios gastos
- Validación de formularios

---

## Estructura

```
src/main/java/com/fjconde/expensetracker/
├── config/
├── controller/
├── dto/
├── entity/
├── repository/
└── service/

src/main/resources/
├── templates/
│   ├── auth/          → login.html, registro.html
│   ├── gastos/        → lista.html, form.html
│   └── dashboard.html
└── static/css/
```

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

---

## Consola H2

Disponible en `http://localhost:8080/h2-console` (solo en modo desarrollo).

- JDBC URL: `jdbc:h2:mem:expensetracker`
- Usuario: `sa` · Contraseña: *(vacía)*
