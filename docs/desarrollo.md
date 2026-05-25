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
| JavaScript | ES6 | Dropdown dependiente categoría/subcategoría |
| Chart.js | 4.4 | Gráfica de donut en el dashboard |
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

### Fase 3 — CRUD de gastos con categorías y subcategorías

**Rama:** `feature/gastos`

**Archivos creados:**

| Archivo | Descripción |
|---|---|
| `entity/CategoriaGasto.java` | Enum con 6 categorías principales y su nombre para mostrar |
| `entity/SubcategoriaGasto.java` | Enum con 30 subcategorías, cada una referenciando su categoría padre |
| `entity/Gasto.java` | Entidad JPA con relación `@ManyToOne` al usuario propietario |
| `repository/GastoRepository.java` | `findByUsuarioOrderByFechaDesc`, `findByIdAndUsuario` |
| `dto/GastoDto.java` | DTO del formulario con validaciones `@NotBlank`, `@NotNull`, `@DecimalMin` |
| `service/GastoService.java` | CRUD completo verificando propietario en cada operación |
| `controller/GastoController.java` | Listar, crear, editar, eliminar con `@AuthenticationPrincipal` |
| `templates/gastos/lista.html` | Tabla de gastos con badges de categoría y confirmación de borrado |
| `templates/gastos/form.html` | Formulario único para crear y editar con dropdown dependiente en JS |

**Decisiones de diseño:**

**Un formulario para crear y editar.** En vez de tener `nuevo.html` y `editar.html` por separado, el mismo `form.html` sirve para los dos casos. El controlador pasa `gastoId` al modelo cuando es una edición (null cuando es nuevo), y Thymeleaf ajusta el título, la action del formulario y el texto del botón con expresiones condicionales: `th:text="${gastoId} ? 'Guardar cambios' : 'Añadir gasto'"`.

**`findByIdAndUsuario` como medida de seguridad.** Si el repositorio solo tuviera `findById`, un usuario podría editar o eliminar el gasto de otro usuario cambiando el ID en la URL (ej: `/gastos/5/editar`). Con `findByIdAndUsuario` Spring Data genera una query con `WHERE id = ? AND usuario_id = ?`, por lo que si el gasto no pertenece al usuario autenticado devuelve `Optional.empty()` y el servicio lanza una excepción.

**`@AuthenticationPrincipal` en el controlador.** En vez de obtener el email de `SecurityContextHolder` y luego buscar el usuario en base de datos, `@AuthenticationPrincipal Usuario usuario` inyecta directamente el objeto `Usuario` que ya cargó Spring Security al hacer login. Evita una consulta extra por petición.

**Los formularios HTML no soportan DELETE ni PUT.** Los navegadores solo envían `GET` y `POST` en formularios. Por eso el endpoint de borrado es `POST /gastos/{id}/eliminar` en vez de `DELETE /gastos/{id}`. Es el enfoque estándar en aplicaciones Thymeleaf.

**BigDecimal para importes monetarios.** Los tipos `float` y `double` tienen problemas de precisión en base 2 (ej: `0.1 + 0.2 = 0.30000000000000004`). Para dinero siempre se usa `BigDecimal`, que trabaja en base 10 y garantiza exactitud en las operaciones aritméticas.

**Dropdown dependiente con JavaScript:**

El formulario tiene dos selects: categoría y subcategoría. Cuando el usuario cambia la categoría, el segundo select debe actualizarse mostrando solo las subcategorías de esa categoría.

Para pasar los datos de Java a JavaScript se usa la sintaxis de Thymeleaf inline:

```html
<script th:inline="javascript">
    const subcategoriasPorCategoria = /*[[${subcategoriasPorCategoria}]]*/ {};
</script>
```

El controlador construye un `Map<String, List<Map<String, String>>>` con la estructura `{ "VIVIENDA": [{value: "HIPOTECA_ALQUILER", label: "Hipoteca o alquiler"}, ...], ... }`. Thymeleaf serializa ese mapa a JSON automáticamente al renderizar la plantilla. El resultado en el HTML es:

```javascript
const subcategoriasPorCategoria = {"VIVIENDA":[{"value":"HIPOTECA_ALQUILER","label":"Hipoteca o alquiler"}, ...],...};
```

JavaScript escucha el evento `change` del primer select y reconstruye las opciones del segundo:

```javascript
selectCategoria.addEventListener('change', function () {
    actualizarSubcategorias(this.value, null);
});
```

En modo edición, también se preselecciona la subcategoría que ya tenía el gasto cargando el valor guardado desde Thymeleaf (`subcategoriaSeleccionada`) y marcando `option.selected = true` cuando coincide.

**Resultado:** CRUD completo funcionando. El usuario puede añadir gastos con categoría y subcategoría, editarlos y eliminarlos. La tabla muestra los gastos ordenados por fecha con badge de categoría e importe formateado.

---

### Fase 4 — Dashboard con ingresos, estadísticas y gráfica Chart.js

**Rama:** `feature/dashboard`

**Archivos creados:**

| Archivo | Descripción |
|---|---|
| `entity/TipoIngreso.java` | Enum con 8 tipos de ingreso |
| `entity/Ingreso.java` | Entidad JPA con relación `@ManyToOne` al usuario |
| `repository/IngresoRepository.java` | `findByUsuarioOrderByFechaDesc`, `findByIdAndUsuario` |
| `dto/IngresoDto.java` | DTO del formulario con validaciones |
| `service/IngresoService.java` | CRUD completo de ingresos |
| `controller/IngresoController.java` | Listar, crear, editar, eliminar ingresos |
| `templates/ingresos/lista.html` | Tabla de ingresos con badges verdes |
| `templates/ingresos/form.html` | Formulario único para crear y editar |

**Archivos modificados:**

| Archivo | Cambios |
|---|---|
| `controller/DashboardController.java` | Calcula estadísticas del mes y las pasa al modelo |
| `templates/dashboard.html` | Dashboard completo con tarjetas, gráfica Chart.js y últimos gastos |
| `templates/fragments/navbar.html` | Añadido enlace a Ingresos |

**Decisiones de diseño:**

**Las estadísticas se calculan en Java, no en SQL.** En vez de escribir queries con `SUM`, `GROUP BY` y `WHERE fecha BETWEEN`, los cálculos se hacen en el servicio/controlador usando Java Streams sobre los datos ya cargados. Para el volumen de datos de un usuario personal esto es más que suficiente y el código queda más legible. Si la app escalara a miles de usuarios, sería preferible delegar los cálculos a la base de datos.

```java
// Filtrar gastos del mes actual
List<Gasto> gastosDelMes = gastoService.obtenerGastos(usuario).stream()
    .filter(g -> g.getFecha().getMonthValue() == mesActual
              && g.getFecha().getYear() == anioActual)
    .collect(Collectors.toList());

// Sumar importes con reduce
BigDecimal totalGastos = gastosDelMes.stream()
    .map(Gasto::getImporte)
    .reduce(BigDecimal.ZERO, BigDecimal::add);

// Agrupar por categoría con Collectors.groupingBy
Map<CategoriaGasto, BigDecimal> gastosPorCategoria = gastosDelMes.stream()
    .collect(Collectors.groupingBy(
        Gasto::getCategoria,
        Collectors.reducing(BigDecimal.ZERO, Gasto::getImporte, BigDecimal::add)
    ));
```

**Balance con color condicional en Thymeleaf.** La tarjeta de balance muestra el número en verde o rojo según su valor. Thymeleaf permite aplicar clases CSS de forma condicional:

```html
<h4 th:classappend="${balance >= 0} ? 'balance-positivo' : 'balance-negativo'"
    th:text="${#numbers.formatDecimal(balance, 1, 2)} + ' €'"></h4>
```

**Gráfica de donut con Chart.js.** Chart.js es una librería JavaScript que genera gráficas en un elemento `<canvas>`. Se carga desde CDN. Los datos (etiquetas y valores) se pasan desde el controlador como listas y se inyectan en el script con Thymeleaf inline:

```javascript
const labels = /*[[${chartLabels}]]*/ [];
const datos  = /*[[${chartData}]]*/ [];

new Chart(ctx, {
    type: 'doughnut',
    data: { labels: labels, datasets: [{ data: datos, ... }] },
    options: { ... }
});
```

El tooltip se personaliza con la función `callbacks.label` para mostrar el importe en euros al pasar el ratón sobre cada sector.

**Resultado:** Dashboard completo con 4 tarjetas estadísticas, gráfica de donut interactiva por categoría y listado de los últimos 5 gastos. CRUD de ingresos funcionando con 8 tipos.

---

*Proyecto completo.*
