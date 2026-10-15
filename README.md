# rewards-service

A small loyalty points service: members earn points and (soon) redeem them for rewards.

## Stack

Java 21 · Spring Boot 3.5 · Spring WebFlux · Spring Data R2DBC · PostgreSQL · Testcontainers

## Requirements

- JDK 21
- Maven 3.9+ (or your IDE's bundled Maven)
- Docker running (tests start PostgreSQL in a container)

## Run the tests

```bash
mvn test
```

The first run downloads dependencies and the `postgres:16-alpine` image, so it can take a few minutes.

## Run the app locally

```bash
mvn spring-boot:test-run
```

This starts PostgreSQL in Docker and the API on http://localhost:8080.

## Existing API

| Method | Path | Body | Response |
|---|---|---|---|
| `POST` | `/accounts/{accountId}/earn` | `{ "points": 500 }` | `200 { "accountId": "...", "balance": 500 }` — creates the account on first earn |
| `GET` | `/accounts/{accountId}` | – | `200 { "accountId": "...", "balance": 500 }` or `404` |

## Layout

```
account/   controller, service, repository (DatabaseClient), DTOs
web/       exception → HTTP problem mapping
resources/schema.sql   tables, applied on startup
```

Your task is in **TASK.md**.
