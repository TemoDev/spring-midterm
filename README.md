# Library Management API

Spring boot project for Spring miderm. 

## Tech Stack

- Java 
- Spring Boot 
- Spring Web, Spring Data JPA, Spring Validation
- PostgreSQL
- Lombok
- Swagger UI
- Maven

## Swagger UI

Once the app is running, open:

```
http://localhost:8080/swagger-ui.html
```

## API Endpoints

| Method | Path                  | Description           | Success status |
|--------|-----------------------|-----------------------|----------------|
| POST   | `/api/authors`        | Create author         | 201 Created    |
| GET    | `/api/authors`        | List all authors      | 200 OK         |
| GET    | `/api/authors/{id}`   | Get author by ID      | 200 OK / 404   |
| PUT    | `/api/authors/{id}`   | Update author         | 200 OK / 404   |
| DELETE | `/api/authors/{id}`   | Delete author         | 204 No Content |
| POST   | `/api/books`          | Create book           | 201 Created    |
| GET    | `/api/books`          | List all books        | 200 OK         |
| GET    | `/api/books/{id}`     | Get book by ID        | 200 OK / 404   |
| PUT    | `/api/books/{id}`     | Update book           | 200 OK / 404   |
| DELETE | `/api/books/{id}`     | Delete book           | 204 No Content |

## Error Responses

The global exception handler returns consistent error payloads:

| Status | Trigger                                                     |
|--------|-------------------------------------------------------------|
| 400    | Validation failure on `@Valid @RequestBody` (field-level)   |
| 404    | `ResourceNotFoundException` thrown from the service layer   |
| 409    | Unique constraint violation (duplicate email / ISBN)        |
| 500    | Any other unhandled exception                               |

## Project Structure

```
src/main/java/com/example/library/
├── LibraryApplication.java       # @SpringBootApplication entry point
├── config/
│   └── OpenApiConfig.java        # Custom OpenAPI title / version / description
├── controller/                   # @RestController layer (no business logic)
│   ├── AuthorController.java
│   └── BookController.java
├── service/                      # @Service layer (business logic + @Transactional)
│   ├── AuthorService.java
│   └── BookService.java
├── repository/                   # JpaRepository interfaces
│   ├── AuthorRepository.java
│   └── BookRepository.java
├── entity/                       # @Entity JPA classes
│   ├── Author.java               # 1 - N Book
│   └── Book.java                 # N ─ 1 Author
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
    └── GlobalExceptionHandler.java   # @RestControllerAdvice
```

## Architectural Rules

- **Controllers** contain no business logic — they only delegate to services and return `ResponseEntity<DTO>`.
- **Services** hold all business logic and orchestration, annotated with `@Transactional` (`readOnly = true` for reads).
- **Repositories** extend `JpaRepository` and handle data access only.
- Entities **never** leave the controller layer — every response is mapped to a `*ResponseDTO`.
- Request bodies are **never** entities — every input is a `*RequestDTO` or `*UpdateDTO` with `@Valid` validation.


