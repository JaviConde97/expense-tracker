# Documentación técnica — Expense Tracker

Decisiones de arquitectura, orden de implementación y notas del proceso de desarrollo.

---

## Stack

| Tecnología | Versión | Rol |
|---|---|---|
| Java | 21 | Lenguaje |
| Spring Boot | 3.5 | Framework principal |
| Spring Security | (incluido en Boot) | Autenticación por sesión |
| Thymeleaf | (incluido en Boot) | Motor de plantillas HTML |
| thymeleaf-extras-springsecurity6 | (incluido en Boot) | Integración Security + Thymeleaf |
| Bootstrap | 5 | Estilos CSS |
| Spring Data JPA | (incluido en Boot) | Acceso a datos |
| H2 | (incluido en Boot) | Base de datos en memoria (dev) |
| MySQL | 8+ | Base de datos (prod) |
| Lombok | (incluido en Boot) | Reducción de boilerplate |
| Maven | 3.8+ | Gestión de dependencias |

---

## Diferencia clave con task-manager-api

El task-manager-api es una **API REST pura**: devuelve JSON y usa JWT para autenticación.

Este proyecto es una **aplicación web tradicional**: devuelve páginas HTML renderizadas en servidor (Thymeleaf) y usa sesión HTTP para autenticación. Es el enfoque que se usa en el trabajo con Spring + Thymeleaf.

| | task-manager-api | expense-tracker |
|---|---|---|
| Controladores | `@RestController` | `@Controller` |
| Respuesta | JSON | HTML (plantillas Thymeleaf) |
| Autenticación | JWT (Bearer token) | Sesión HTTP (cookie) |
| Cliente | Postman / frontend SPA | Navegador directamente |

---

## Fases de desarrollo

### Fase 1 — Setup inicial

**Rama:** `main` (commit inicial)

**Proceso de creación del proyecto:**

El proyecto se genera desde [Spring Initializr](https://start.spring.io) con la siguiente configuración:

- **Project:** Maven
- **Language:** Java
- **Spring Boot:** 3.5.x
- **Group:** `com.fjconde`
- **Artifact:** `expense-tracker`
- **Packaging:** Jar
- **Java:** 21

**Dependencias seleccionadas en Spring Initializr:**

| Dependencia | Motivo |
|---|---|
| Spring Web | Para los controladores y el servidor embebido |
| Thymeleaf | Motor de plantillas para renderizar HTML |
| Spring Security | Gestión de autenticación y autorización |
| Spring Data JPA | ORM para acceder a la base de datos |
| H2 Database | Base de datos en memoria para desarrollo |
| MySQL Driver | Driver para producción con MySQL |
| Validation | Anotaciones @NotBlank, @Email, etc. en formularios |
| Lombok | Genera getters, setters y constructores automáticamente |
| Spring Boot DevTools | Recarga automática al modificar código en desarrollo |

Adicionalmente se añade a mano en el `pom.xml`:

```xml
<!-- Necesario para usar sec:authorize en las plantillas Thymeleaf -->
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>
```

Esta dependencia no aparece en Spring Initializr pero es imprescindible para mostrar u ocultar elementos HTML según si el usuario está autenticado o tiene cierto rol.

**Perfiles configurados:**

- `application.properties` — configuración compartida (perfil activo: `dev`)
- `application-dev.properties` — H2 en memoria, consola H2 activa, DDL `create-drop`
- `application-prod.properties` — MySQL con variables de entorno, DDL `update`

**Resultado:** El proyecto arranca correctamente en `http://localhost:8080` (muestra error 403 porque Spring Security ya está activo y no hay páginas públicas aún — comportamiento esperado).

---

*Proyecto en desarrollo.*
