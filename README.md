# Hostera Platform

RESTful Web Services of **Hostera**, the hotel operations platform built by **Team Coworkers**
for the course 1ASI0729 Desarrollo de Aplicaciones Open Source (UPC, 2026-20).

The API serves the [Hostera Frontend Web Application](https://github.com/Team-Coworkers/hostera-frontend)
and follows Domain-Driven Design with one package per bounded context.

## Technology stack

- Java 25 and Spring Boot 4.1 (Web, Data JPA, Validation, Security)
- PostgreSQL (H2 in memory for the automated tests)
- JWT authentication with jjwt and BCrypt password hashing
- OpenAPI 3 documentation with springdoc-openapi and Swagger UI
- Lombok and Pluralize; Docker multi-stage image for deployment

## Bounded contexts

| Package | Responsibility | Resources |
| --- | --- | --- |
| `iam` | Sign-up, sign-in with JWT, users and roles | `/api/v1/users`, `/api/v1/tokens`, `/api/v1/roles` |
| `organizations` | Hotel properties and their staff | `/api/v1/properties`, `/api/v1/staff-members` |
| `bookings` | Bookings and payments | `/api/v1/bookings`, `/api/v1/payments` |
| `rooms` | Room types, rooms, rate plans, daily rates and status periods | `/api/v1/room-types`, `/api/v1/rooms`, `/api/v1/rate-plans`, `/api/v1/daily-rates`, `/api/v1/status-periods` |
| `inventory` | Inventory items and storage locations | `/api/v1/inventory-items`, `/api/v1/storage-locations` |
| `accesscontrol` | Key-card credentials and access events | `/api/v1/credentials`, `/api/v1/access-events` |
| `shared` | Result type, errors as Problem Details, i18n, OpenAPI, naming strategy and auditing | — |

Each context is layered as `domain`, `application`, `infrastructure` and `interfaces`, the same
structure as the course reference project.

## Run locally

1. Start PostgreSQL, for example with Docker:

   ```bash
   docker run -d --name hostera-db -e POSTGRES_DB=hostera -e POSTGRES_PASSWORD=password -p 5432:5432 postgres:17
   ```

2. Run the API with the `dev` profile (the default):

   ```bash
   mvn spring-boot:run
   ```

3. Open Swagger UI at <http://localhost:8080/swagger-ui/index.html>.

The `dev` profile disables authentication unless `SECURITY_ENABLED=true`. Copy `.env.example`
to see every supported variable.

## Tests

```bash
mvn test
```

## Deployment

The `Dockerfile` builds the JAR and runs it with the `prod` profile. The hosting service must
define `DATABASE_URL`, `DATABASE_PORT`, `DATABASE_NAME`, `DATABASE_USER`, `DATABASE_PASSWORD`,
`JWT_SECRET` and `CORS_ALLOWED_ORIGINS`. Secrets never go into the repository.

## Git workflow

GitFlow with Conventional Commits and Semantic Versioning: `feature/*` branches start from
`develop` and merge back with `--no-ff`; `release/x.y.z` merges into `main` with a `vx.y.z` tag.

## License

[MIT](LICENSE.md)
