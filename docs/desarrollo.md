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

### Fase 2 — Autenticación con sesión HTTP

**Rama:** `feature/auth`

**Archivos creados:**

| Archivo | Descripción |
|---|---|
| `entity/Usuario.java` | Entidad JPA que implementa `UserDetails` directamente |
| `repository/UsuarioRepository.java` | `findByEmail`, `existsByEmail` |
| `dto/RegistroDto.java` | DTO del formulario de registro con validaciones |
| `service/UsuarioService.java` | Implementa `UserDetailsService`, gestiona el registro |
| `config/SecurityConfig.java` | Configuración de Spring Security con sesión HTTP |
| `controller/AuthController.java` | GET/POST de login y registro |
| `controller/DashboardController.java` | Placeholder del dashboard tras el login |
| `templates/auth/login.html` | Formulario de login con Bootstrap 5 |
| `templates/auth/registro.html` | Formulario de registro con validaciones inline |
| `templates/fragments/navbar.html` | Navbar reutilizable con `sec:authorize` |
| `templates/dashboard.html` | Dashboard placeholder |

**Decisiones de diseño:**

**Usuario implementa UserDetails directamente.** En vez de tener una clase `UserDetailsWrapper` separada, la entidad `Usuario` implementa la interfaz `UserDetails`. Es el enfoque más directo para proyectos sin roles complejos — evita una clase extra y Spring Security puede usarla sin adaptadores.

**El login lo procesa Spring Security, no un controlador.** En el `AuthController` solo existe el `GET /auth/login` para mostrar el formulario. El `POST /auth/login` lo intercepta Spring Security automáticamente según lo configurado en `SecurityConfig` con `.loginProcessingUrl("/auth/login")`. Intentar crear un `@PostMapping("/auth/login")` causaría conflicto.

**CSRF activo (a diferencia del task-manager-api).** En la API REST desactivamos CSRF porque usábamos JWT. Aquí lo mantenemos activo porque es una app web con formularios y sesión — la protección CSRF es necesaria. Solo se desactiva para `/h2-console/**` que no usa formularios propios.

**Bootstrap 5 via CDN.** Los estilos se cargan desde `cdn.jsdelivr.net` directamente en cada plantilla. No hay ficheros CSS propios por ahora — se añadirán en fases posteriores si se necesita personalización.

**Fragmentos Thymeleaf para la navbar.** La navbar se define una sola vez en `fragments/navbar.html` y se incluye en cada página con `th:replace="~{fragments/navbar :: navbar}"`. Así cualquier cambio en la navbar se aplica a toda la app automáticamente.

**Resultado:** Registro, login y logout funcionando. Tras el login el usuario llega al dashboard. Los mensajes de error (credenciales incorrectas, email ya en uso) se muestran en el propio formulario sin recargar desde cero.

**Cómo probar la autenticación en desarrollo:**

1. Arrancar la app con `./mvnw spring-boot:run` (perfil `dev` activo por defecto)
2. Ir a `http://localhost:8080` — redirige automáticamente a `/auth/login`
3. Hacer clic en "Regístrate aquí" para crear una cuenta nueva
4. La base de datos H2 es en memoria: se vacía cada vez que se reinicia la app, así que hay que registrarse de nuevo en cada arranque
5. Tras registrarse redirige a login con mensaje de confirmación
6. Iniciar sesión con el email y contraseña registrados → accede al dashboard

**Errores encontrados y soluciones:**

**Error: dependencia circular al arrancar**

```
The dependencies of some of the beans in the application context form a cycle:
securityConfig → usuarioService → securityConfig
```

Causa: `SecurityConfig` inyectaba `UsuarioService` (para autenticación) y a la vez definía el bean `PasswordEncoder`. `UsuarioService` a su vez necesitaba ese `PasswordEncoder`. Spring no puede crear ninguno de los dos porque cada uno espera al otro.

Solución: extraer el `PasswordEncoder` a una clase de configuración separada (`AppConfig.java`). Al estar en su propia clase sin dependencias, Spring lo crea primero y el ciclo desaparece:

```
AppConfig      → crea PasswordEncoder (sin dependencias)
UsuarioService → depende de PasswordEncoder ✅
SecurityConfig → depende de UsuarioService  ✅
```

**Error: items de la navbar apilados verticalmente**

Causa: usar `<ul class="navbar-nav"><li class="nav-item">` con clases de Bootstrap que aplican `flex-direction: column` por defecto en ciertos contextos.

Solución: sustituir la estructura de lista por un `<div class="d-flex align-items-center gap-3 ms-auto">` con los enlaces directamente dentro. Más simple y garantiza que todo quede en una línea horizontal.

---

*Proyecto en desarrollo.*
