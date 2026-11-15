# Sonido Vivo — Backend

API REST monolítica de [Sonido Vivo](https://github.com/MainSteps/sonido-vivo-react), tienda de instrumentos y audio. El proyecto corresponde a DSY1104 Full Stack II (Duoc UC, 2026-2) y cubre el registro de backend previo a la Evaluación Parcial 3.

El front que consume esta API es React + Vite + axios y corre en `http://localhost:5173`.

> El esqueleto de la aplicación (Spring Initializr, capas, Compose y Flyway) se arma en una sesión aparte. Esta documentación describe lo planificado; las secciones marcadas como **próximamente** aplican cuando el código exista en el repositorio.

## Stack

| Pieza | Tecnología |
| --- | --- |
| Lenguaje | Java 21 |
| Framework | Spring Boot 3 |
| Persistencia | PostgreSQL, Spring Data JPA y Flyway |
| Seguridad | Spring Security, JWT y roles `ADMIN` y `CLIENTE`; contraseñas con BCrypt |
| Validación | Bean Validation y manejo global de excepciones |
| Documentación | springdoc-openapi (Swagger UI) |
| Pruebas | JUnit 5 y Mockito |
| Construcción | Maven (wrapper `./mvnw`) |
| Entorno local | Docker Compose para PostgreSQL |

## Arquitectura

Arquitectura monolítica por capas. El paquete base planificado es `cl.sonidovivo.api`:

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

Los perfiles previstos son `local` (desarrollo con Compose) y, más adelante, uno de despliegue en la nube. El despliegue en la nube y el espejo privado quedan como extras; lo evaluado corre en local.

## Endpoints planificados

| Método | Ruta | Descripción | Acceso |
| --- | --- | --- | --- |
| `GET` | `/api/health` | Estado de la API | Público |
| `POST` | `/api/auth/register` | Registro de usuario | Público |
| `POST` | `/api/auth/login` | Inicio de sesión; devuelve un JWT | Público |
| `GET` | `/api/products` | Listar productos | Público |
| `GET` | `/api/products/{id}` | Obtener un producto | Público |
| `POST` | `/api/products` | Crear producto | `ADMIN` |
| `PUT` | `/api/products/{id}` | Actualizar producto | `ADMIN` |
| `DELETE` | `/api/products/{id}` | Eliminar producto | `ADMIN` |
| `POST` | `/api/contact` | Enviar mensaje de contacto | Público |
| `GET` | `/api/contact` | Listar mensajes de contacto | `ADMIN` |

El token JWT se envía en la cabecera `Authorization: Bearer <token>`.

## Cómo ejecutarlo en local

**Próximamente.** Cuando el esqueleto esté en el repositorio, el flujo será:

1. Copia las variables de entorno de ejemplo:

   ```bash
   cp .env.example .env
   ```

2. Levanta PostgreSQL con Docker Compose:

   ```bash
   docker compose up -d
   ```

3. Arranca la API con el wrapper de Maven:

   ```bash
   ./mvnw spring-boot:run
   ```

La API quedará en `http://localhost:8080`. Swagger UI estará en [`/swagger-ui.html`](http://localhost:8080/swagger-ui.html) y deberá incluir el botón **Authorize** para probar peticiones con el Bearer token.

## Variables de entorno

Definidas en `.env.example`. No subas un archivo `.env` con secretos reales.

| Variable | Uso | Ejemplo (no secreto) |
| --- | --- | --- |
| `DB_URL` | JDBC de PostgreSQL | `jdbc:postgresql://localhost:5432/sonido_vivo` |
| `DB_USER` | Usuario de la base | `sonido` |
| `DB_PASSWORD` | Contraseña de la base | `sonido` |
| `JWT_SECRET` | Clave para firmar el JWT | `cambia-este-secreto-de-ejemplo` |
| `JWT_EXPIRATION` | Expiración del token, en milisegundos | `3600000` |
| `CORS_ORIGINS` | Orígenes permitidos | `http://localhost:5173` |

## Flujo de trabajo

Este repositorio lo desarrolla una sola persona. Aun así, cada cambio entra por una rama y un pull request, para dejar el historial de `main` limpio y poder cerrar issues al fusionar.

1. Crea una rama por issue, con el formato `feat/<n>-descripcion-corta` (por ejemplo `feat/3-crud-productos`). Para correcciones usa `fix/<n>-...`.
2. Abre un pull request hacia `main` e incluye `Closes #<n>` en la descripción.
3. El merge es **squash**. La rama se borra después del merge.

Antes de abrir el PR, confirma que el proyecto compila, que los tests pasan, que Swagger quedó actualizado si cambiaste endpoints y que no hay secretos en el diff.

## Autor

Diego Alonso Pazos Atarama — Duoc UC, DSY1104 Full Stack II (2026-2).

Licencia [MIT](LICENSE).
