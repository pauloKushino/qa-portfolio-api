# QA Portfolio API

![CI/CD](https://github.com/pauloKushino/qa-portfolio-api/actions/workflows/ci-cd.yml/badge.svg)
![Coverage](https://img.shields.io/badge/Coverage_gate-80%25-brightgreen)
![Java](https://img.shields.io/badge/Java-21-blue?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen?logo=spring)

Backend showcase demonstrating **QA automation skills** with Spring Boot 4: comprehensive test strategy, contract testing, Testcontainers, REST Assured, CI/CD with coverage gates.

> **Demo pública:** ainda não publicada. O deploy está configurado no workflow e no `railway.toml`; enquanto isso, siga as instruções de execução local abaixo.

## Tech Stack

- **Java 21** + **Spring Boot 4.1.1**
- **Spring Data JPA** (Hibernate) + **Flyway** migrations
- **PostgreSQL 17** (Testcontainers for tests, Neon/Railway for prod)
- **SpringDoc OpenAPI 3** (Swagger UI)
- **Lombok** for boilerplate reduction

## Test Strategy (The Showcase)

| Layer | Tools | Coverage Target |
|-------|-------|-----------------|
| **Unit** | JUnit 5 + Mockito | 80%+ (enforced by JaCoCo) |
| **Integration** | `@SpringBootTest` + **Testcontainers** (real PostgreSQL) | Repository & service integration |
| **Contract** | **REST Assured** | Full API contract validation |
| **CI/CD** | GitHub Actions + **JaCoCo** + **Codecov** | Quality gate on every PR |

## Domain Model

```
Project → TestSuite → TestCase → TestRun → TestResult
                ↓
Project → Bug
```

- **Project**: Portfolio project (repo, demo, tech stack)
- **TestSuite**: Group of tests (unit, integration, contract, e2e)
- **TestCase**: Individual test case with status, priority, tags
- **TestRun**: Execution of a suite with results summary
- **TestResult**: Individual test result (passed/failed/skipped)
- **Bug**: Defect tracking linked to project/test case

## API Endpoints

| Resource | Endpoints |
|----------|-----------|
| Projects | `POST /api/projects`, `GET /api/projects`, `GET /api/projects/{id}`, `PUT /api/projects/{id}`, `DELETE /api/projects/{id}` |
| Test Suites | `POST /api/projects/{projectId}/test-suites`, `GET /api/projects/{projectId}/test-suites`, `GET/PUT/DELETE /api/test-suites/{id}` |
| Test Cases | `POST /api/test-suites/{suiteId}/test-cases`, `GET /api/test-suites/{suiteId}/test-cases`, `GET/PUT/DELETE /api/test-cases/{id}` |
| Test Runs | `POST /api/test-suites/{suiteId}/test-runs`, `POST /api/test-runs/{id}/start`, `POST /api/test-runs/{id}/finish`, `GET /api/test-suites/{suiteId}/test-runs` |
| Test Results | `POST /api/test-runs/{runId}/results`, `GET /api/test-runs/{runId}/results` |
| Bugs | `POST /api/projects/{projectId}/bugs`, `GET /api/projects/{projectId}/bugs`, `GET/PUT/POST /api/bugs/{id}/resolve`, `POST /api/bugs/{id}/close`, `DELETE /api/bugs/{id}` |

## Running Locally

**Prerequisites:** Java 21, Docker Desktop (for PostgreSQL), or local PostgreSQL.

```bash
# Start PostgreSQL
docker run -d --name postgres-qa \
  -e POSTGRES_DB=qa_portfolio \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 postgres:17

# Run application
./mvnw spring-boot:run
```

API: http://localhost:8080
Swagger: http://localhost:8080/swagger-ui.html

## Running Tests

```bash
# All tests (unit + integration + contract) with coverage
./mvnw verify

# Unit tests only
./mvnw test

# Integration tests only (requires Docker)
./mvnw test -Dtest=*IntegrationTest

# Contract tests only (requires Docker)
./mvnw test -Dtest=*ContractTest

# Coverage report
open target/site/jacoco/index.html
```

## CI/CD Pipeline

```mermaid
graph LR
    A[Push/PR] --> B[Test Job]
    B --> C[Unit Tests]
    B --> D[Integration Tests<br/>(Testcontainers)]
    B --> E[Contract Tests<br/>(REST Assured)]
    B --> F[JaCoCo Coverage ≥80%]
    F --> G[Codecov Upload]
    G --> H[Build Job]
    H --> I[Package JAR]
    I --> J[Deploy to Railway<br/>(DEPLOY_ENABLED=true)]
```

**Quality Gates:**
- All tests must pass
- Line coverage ≥ 80% (JaCoCo `check` goal)
- Coverage uploaded to Codecov
- Deploy only on `main` branch

## Project Structure

```
src/
├── main/
│   ├── java/com/paulo/qa/portfolio/
│   │   ├── controller/     # REST endpoints (OpenAPI annotated)
│   │   ├── service/        # Business logic @Transactional
│   │   ├── repository/     # Spring Data JPA
│   │   ├── entity/         # JPA entities
│   │   ├── dto/            # Request/Response DTOs (validated)
│   │   ├── exception/      # Global @RestControllerAdvice
│   │   └── QaPortfolioApiApplication.java
│   └── resources/
│       ├── application.yml
│       ├── application-test.yml
│       └── db/migration/   # Flyway V1__create_initial_schema.sql
└── test/
    ├── java/com/paulo/qa/portfolio/
    │   ├── service/        # Unit tests (Mockito)
    │   ├── integration/    # @SpringBootTest + Testcontainers
    │   └── contract/       # REST Assured API tests
```

## Deploy to Railway

1. Create project at [railway.app](https://railway.app)
2. Add PostgreSQL service
3. Connect GitHub repo
4. Set environment variables:
   - `DATABASE_URL` (from Railway PostgreSQL)
   - `RAILWAY_TOKEN` (in GitHub repo secrets)
   - `DEPLOY_ENABLED=true` (in GitHub repo variables)
5. Push to `main` → runs tests and deploys when `DEPLOY_ENABLED=true`

## Why This Showcases QA Skills

1. **Test Pyramid** properly implemented (unit → integration → contract)
2. **Testcontainers** for real DB integration tests (no H2 mocking)
3. **REST Assured** for contract testing - validates actual HTTP contracts
4. **Coverage as quality gate** - JaCoCo fails build if < 80%
5. **CI/CD with quality gates** - no deploy without passing tests + coverage
6. **OpenAPI/Swagger** - living documentation, contract-first ready
7. **Flyway migrations** - versioned schema, reproducible environments
8. **Global error handling** - consistent API error responses
9. **Validation** - Bean Validation on all DTOs
10. **Realistic domain** - QA portfolio tracking (projects, suites, cases, runs, bugs)

---

*Built as a portfolio piece to demonstrate backend + QA automation proficiency.*