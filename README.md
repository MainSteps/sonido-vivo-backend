# Sonido Vivo — Backend

API REST monolítica de [Sonido Vivo](https://github.com/MainSteps/sonido-vivo-react), tienda de instrumentos y audio. El proyecto corresponde a DSY1104 Full Stack II (Duoc UC, 2026-2) y cubre el registro de backend previo a la Evaluación Parcial 3.

El front que consume esta API es React + Vite + axios y corre en `http://localhost:5173`.

## Stack

| Pieza | Tecnología |
| --- | --- |
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.5 |
| Persistencia | PostgreSQL (producción), H2 en memoria (perfil `dev`), Spring Data JPA y Flyway |
| Seguridad | Spring Security, JWT y roles `ADMIN` y `CLIENTE`; contraseñas con BCrypt |
| Validación | Bean Validation y manejo global de excepciones |
| Documentación | springdoc-openapi (Swagger UI) |
| Pruebas | JUnit 5 y Mockito |
| Construcción | Maven (wrapper `./mvnw` o `mvnw.cmd`) |

## Arquitectura

Arquitectura monolítica por capas. El paquete base es `cl.sonidovivo.api`:

```text
cl.sonidovivo.api
├── config        # configuración de la aplicación (CORS, OpenAPI, perfiles)
├── security      # JWT, filtros y reglas de autorización
├── controller    # endpoints REST
├── service       # lógica de negocio
├── repository    # acceso a datos (Spring Data JPA)
├── entity        # entidades JPA
├── dto           # objetos de transferencia y validación
├── mapper        # conversión entre entidad y DTO
└── exception     # manejo global de errores
```

El perfil activo por defecto es `dev` (H2 en modo PostgreSQL). En el servidor de producción se usa el perfil `prod` con PostgreSQL y variables de entorno.

## Cómo ejecutarlo en local

Requisitos: **Java 21**. No hace falta Docker Compose para desarrollar: el perfil `dev` usa H2 en memoria.

1. Clona el repositorio y entra al directorio del proyecto.

2. Compila y ejecuta los tests:

   ```bash
   ./mvnw clean verify
   ```

   En Windows:

   ```bash
   mvnw.cmd clean verify
   ```

3. Arranca la API:

   ```bash
   ./mvnw spring-boot:run
   ```

La API queda en `http://127.0.0.1:8080`. La consola H2 está en [`/h2-console`](http://127.0.0.1:8080/h2-console) (solo perfil `dev`). Swagger UI estará en [`/swagger-ui.html`](http://127.0.0.1:8080/swagger-ui.html) cuando se complete la documentación OpenAPI.

### Health check

```bash
curl http://127.0.0.1:8080/api/health
```

Respuesta esperada:

```json
{"status":"UP","app":"sonido-vivo-backend"}
```

## Endpoints planificados

| Método | Ruta | Descripción | Acceso |
| --- | --- | --- | --- |
| `GET` | `/api/health` | Estado de la API | Público |
| `POST` | `/api/auth/registro` | Registro de usuario | Público |
| `POST` | `/api/auth/login` | Inicio de sesión; devuelve un JWT | Público |
| `GET` | `/api/productos` | Listar productos | Público |
| `GET` | `/api/productos/{id}` | Obtener un producto | Público |
| `POST` | `/api/productos` | Crear producto | `ADMIN` |
| `PUT` | `/api/productos/{id}` | Actualizar producto | `ADMIN` |
| `DELETE` | `/api/productos/{id}` | Eliminar producto | `ADMIN` |
| `POST` | `/api/contacto` | Enviar mensaje de contacto | Público |
| `GET` | `/api/contacto` | Listar mensajes de contacto | `ADMIN` |

El token JWT se envía en la cabecera `Authorization: Bearer <token>`.

## Variables de entorno (perfil `prod`)

Definidas en `.env.example`. No subas un archivo `.env` con secretos reales.

| Variable | Uso | Ejemplo (no secreto) |
| --- | --- | --- |
| `DB_URL` | JDBC de PostgreSQL | `jdbc:postgresql://localhost:5432/sonido_vivo` |
| `DB_USER` | Usuario de la base | `sonido` |
| `DB_PASSWORD` | Contraseña de la base | `sonido` |
| `JWT_SECRET` | Clave para firmar el JWT | `cambia-este-secreto-de-ejemplo` |
| `JWT_EXPIRATION` | Expiración del token, en milisegundos | `3600000` |
| `CORS_ORIGINS` | Orígenes permitidos | `http://localhost:5173` |

En producción, configura estas variables en el servidor (contenedor PostgreSQL en el host, puerto interno 8080).

## Flujo de trabajo

Este repositorio lo desarrolla una sola persona. Aun así, cada cambio entra por una rama y un pull request, para dejar el historial de `main` limpio y poder cerrar issues al fusionar.

1. Crea una rama por issue, con el formato `feat/<n>-descripcion-corta` (por ejemplo `feat/3-crud-productos`). Para correcciones usa `fix/<n>-...`.
2. Abre un pull request hacia `main` e incluye `Closes #<n>` en la descripción.
3. El merge es **squash**. La rama se borra después del merge.

Antes de abrir el PR, confirma que `./mvnw clean verify` pase, que Swagger quedó actualizado si cambiaste endpoints y que no hay secretos en el diff.

## Autor

Diego Alonso Pazos Atarama — Duoc UC, DSY1104 Full Stack II (2026-2).

Licencia [MIT](LICENSE).