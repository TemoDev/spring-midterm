# Library Management API

Spring boot project for Spring miderm.

## Tech Stack

- Java
- Spring Boot
- Spring Web, Spring Data JPA, Spring Validation, Spring Security
- PostgreSQL
- Lombok
- Swagger UI
- Maven

## How to run

```
mvn spring-boot:run
```

Runs on `http://localhost:8080`. From IntelliJ you can also just press Run on `LibraryApplication`.

On first start the schema tables (`users`, `authorities`) are created from `schema.sql`, and the two seed users below are inserted with hashed passwords.

## Login credentials

| Username | Password | Roles            |
|----------|----------|------------------|
| `admin`  | `admin`  | `USER`, `ADMIN`  |
| `user`   | `user`   | `USER`           |

Passwords are stored hashed using the delegating password encoder (BCrypt by default).

## Roles

- **USER** — can read and modify authors and books (`GET`, `POST`, `PUT` on `/api/authors/**` and `/api/books/**`).
- **ADMIN** — everything `USER` can do, **plus** delete authors and books.

## Swagger UI

```
http://localhost:8080/swagger-ui.html
```

Click the **Authorize** button (top right) and enter one of the credentials above. Swagger will attach an `Authorization: Basic ...` header to every request.

## Protected pages / endpoints

| Path                                    | Access rule                       |
|-----------------------------------------|-----------------------------------|
| `/`                                     | public                            |
| `/error`                                | public                            |
| `/swagger-ui.html`, `/swagger-ui/**`    | public                            |
| `/v3/api-docs`, `/v3/api-docs/**`       | public                            |
| `/login`                                | public (Spring's default page)    |
| `/api/authors`, `/api/authors/{id}` — `GET`, `POST`, `PUT` | authenticated (USER or ADMIN) |
| `/api/books`, `/api/books/{id}` — `GET`, `POST`, `PUT`     | authenticated (USER or ADMIN) |
| `DELETE /api/authors/{id}`              | **ADMIN only** (via `@PreAuthorize`) |
| `DELETE /api/books/{id}`                | **ADMIN only** (via `@PreAuthorize`) |

## API Endpoints

| Method | Path                  | Description           | Success status | Auth required |
|--------|-----------------------|-----------------------|----------------|---------------|
| POST   | `/api/authors`        | Create author         | 201 Created    | USER          |
| GET    | `/api/authors`        | List all authors      | 200 OK         | USER          |
| GET    | `/api/authors/{id}`   | Get author by ID      | 200 OK / 404   | USER          |
| PUT    | `/api/authors/{id}`   | Update author         | 200 OK / 404   | USER          |
| DELETE | `/api/authors/{id}`   | Delete author         | 204 No Content | **ADMIN**     |
| POST   | `/api/books`          | Create book           | 201 Created    | USER          |
| GET    | `/api/books`          | List all books        | 200 OK         | USER          |
| GET    | `/api/books/{id}`     | Get book by ID        | 200 OK / 404   | USER          |
| PUT    | `/api/books/{id}`     | Update book           | 200 OK / 404   | USER          |
| DELETE | `/api/books/{id}`     | Delete book           | 204 No Content | **ADMIN**     |

## ADMIN-only functionality

Deleting authors and books is restricted to `ADMIN`. The restriction is enforced at the **service method level** with `@PreAuthorize("hasRole('ADMIN')")`:

- [`AuthorService.delete(Long id)`](src/main/java/com/example/library/service/AuthorService.java)
- [`BookService.delete(Long id)`](src/main/java/com/example/library/service/BookService.java)

A logged-in `USER` who calls `DELETE /api/authors/1` will receive `403 Forbidden`.

## Error Responses

The global exception handler returns consistent error payloads:

| Status | Trigger                                                          |
|--------|------------------------------------------------------------------|
| 400    | Validation failure on `@Valid @RequestBody` (field-level)        |
| 401    | Missing / invalid credentials on a protected endpoint (Spring Security default) |
| 403    | `AccessDeniedException` from `@PreAuthorize` (e.g. USER tries DELETE) |
| 404    | `ResourceNotFoundException` thrown from the service layer        |
| 409    | Unique constraint violation (duplicate email / ISBN)             |
| 500    | Any other unhandled exception                                    |

## CSRF

CSRF protection is **enabled by default**, with one narrowly-scoped exception:

```java
http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));
```

It's disabled **only for `/api/**`** because that surface is consumed by non-browser clients (curl, Postman, Swagger UI) using HTTP Basic authentication. Those clients don't carry cookies cross-site, so CSRF is not a relevant threat for the REST API. CSRF remains active for the form login flow at `/login`.

## Project Structure

```
src/main/java/com/example/library/
├── LibraryApplication.java       # @SpringBootApplication entry point
├── config/
│   ├── OpenApiConfig.java        # Custom OpenAPI title / version / description + basicAuth scheme
│   ├── SecurityConfig.java       # SecurityFilterChain + JdbcUserDetailsManager + PasswordEncoder
│   └── SeedUsers.java            # Creates admin/admin and user/user on startup
├── controller/                   # @RestController layer (no business logic)
│   ├── AuthorController.java
│   └── BookController.java
├── service/                      # @Service layer (business logic + @Transactional + @PreAuthorize)
│   ├── AuthorService.java
│   └── BookService.java
├── repository/                   # JpaRepository interfaces
│   ├── AuthorRepository.java
│   └── BookRepository.java
├── entity/                       # @Entity JPA classes
│   ├── Author.java               # 1 - N Book
│   └── Book.java                 # N - 1 Author
├── dto/
│   ├── request/                  # Validated input DTOs
│   │   ├── AuthorRequestDTO.java
│   │   ├── AuthorUpdateDTO.java
│   │   ├── BookRequestDTO.java
│   │   └── BookUpdateDTO.java
│   └── response/                 # Output DTOs (entities never leave the controller layer)
│       ├── AuthorResponseDTO.java
│       └── BookResponseDTO.java
├── mapper/                       # Static Entity ↔ DTO mappers
│   ├── AuthorMapper.java
│   └── BookMapper.java
└── exception/
    ├── ResourceNotFoundException.java
    └── GlobalExceptionHandler.java   # @RestControllerAdvice (handles 400/401/403/404/409/500)

src/main/resources/
├── application.properties
└── schema.sql                    # Spring Security users + authorities tables
```

## Architectural Rules

- **Controllers** contain no business logic — they only delegate to services and return `ResponseEntity<DTO>`.
- **Services** hold all business logic and orchestration, annotated with `@Transactional` (`readOnly = true` for reads).
- **Repositories** extend `JpaRepository` and handle data access only.
- Entities **never** leave the controller layer — every response is mapped to a `*ResponseDTO`.
- Request bodies are **never** entities — every input is a `*RequestDTO` or `*UpdateDTO` with `@Valid` validation.
- Authorization rules:
  - **Path-level** in `SecurityConfig` (`/api/**` requires `authenticated()`).
  - **Method-level** via `@PreAuthorize("hasRole('ADMIN')")` on service methods for fine-grained admin-only actions.
