# Sonido Vivo — Backend

API REST monolítica de [Sonido Vivo](https://github.com/MainSteps/sonido-vivo-react), tienda de instrumentos y audio. El proyecto corresponde a DSY1104 Full Stack II (Duoc UC, 2026-2) y cubre el registro de backend previo a la Evaluación Parcial 3.

El front que consume esta API es React + Vite + axios. En desarrollo corre en `http://localhost:5173`.

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

Están definidas en `.env.example` con valores de ejemplo. No subas nunca un `.env` con secretos reales.

| Variable | Uso | Formato o ejemplo (no secreto) |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring | `prod` en el servidor; si no se define, arranca `dev` |
| `DB_URL` | JDBC de PostgreSQL 16 | `jdbc:postgresql://db:5432/<bd>` (`db` es el servicio Postgres del compose del servidor) |
| `DB_USER` | Usuario de la base | `<usuario>` |
| `DB_PASSWORD` | Contraseña de la base | `<contraseña>` |
| `JWT_SECRET` | Clave HS256 para firmar el JWT, de al menos 256 bits y distinta por entorno | `<secreto-aleatorio>` |
| `JWT_EXPIRATION` | Expiración del token en milisegundos | `3600000` |
| `CORS_ORIGINS` | Orígenes permitidos, separados por comas | `<url-del-front>[,<otra>]` |

`http://localhost:5173` se permite solo en el perfil `dev`; en el servidor no va.

## Despliegue en el servidor de demo

La demo corre en un servidor propio dentro de una red privada (Tailscale). No está expuesto a internet. El despliegue es **manual**:

1. Cuando hay una versión lista, se clona `main` en el servidor y se construye con el compose y el Dockerfile **propios del servidor**. Si este repo trae un Dockerfile o un compose, el servidor no los usa. No hay CI, tags ni registro de imágenes.
2. El build es `mvn -B -DskipTests package` con Maven 3.9 y JDK 21 (no usa `mvnw`). Debe quedar **un solo** jar ejecutable en `target/` (el repackage de Spring Boot).
3. El contenedor corre con JRE 21, como usuario no root, con 768 MB de RAM (`MaxRAMPercentage=75`) y sin escritura en disco salvo `/tmp`.
4. La app escucha en `0.0.0.0:8080` dentro del contenedor y se publica como `8081` en la red privada. El front se sirve en el `8080` del mismo servidor.
5. `GET /api/health` es el endpoint de estado.

Reglas que el código debe cumplir para funcionar ahí:

- Todo el estado vive en PostgreSQL. No se guardan archivos en disco local.
- No se llama a servicios externos en tiempo de ejecución; la única dependencia es la base `db`.
- La configuración sale solo de variables de entorno y en el repo no hay secretos: solo `.env.example`, con placeholders.
- El esquema cambia solo con migraciones Flyway nuevas (`V2__...`, `V3__...`), nunca editando una que ya esté en `main`.

## Flujo de trabajo

Este repositorio lo desarrolla una sola persona. Aun así, cada cambio entra por una rama y un pull request, para dejar el historial de `main` limpio y poder cerrar issues al fusionar.

1. Crea una rama por issue, con el formato `feat/<n>-descripcion-corta` (por ejemplo `feat/3-crud-productos`). Para correcciones usa `fix/<n>-...`.
2. Abre un pull request hacia `main` e incluye `Closes #<n>` en la descripción.
3. El merge es **squash**. La rama se borra después del merge.

Antes de abrir el PR, confirma que `./mvnw clean verify` pase, que Swagger quedó actualizado si cambiaste endpoints y que no hay secretos en el diff.

## Autor

Diego Alonso Pazos Atarama — Duoc UC, DSY1104 Full Stack II (2026-2).

Licencia [MIT](LICENSE).