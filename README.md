# academy-catalog-api

Spring Boot based Java sample backend project.

## Overview

- Goal: simple REST API sample using Spring Boot
- Domain: Item CRUD + search + health check
- Baseline: aligned with sample_java1 structure
- Security level: medium hardening baseline (no intentional vulnerabilities)

## Tech Stack

- Java 17 toolchain
- Spring Boot 3.4.2
- Gradle Wrapper 8.7
- Validation: jakarta.validation

## Run

```bash
./gradlew bootRun
```

## Test

```bash
./gradlew test
```

## Build

```bash
./gradlew build
```

## API

- GET /health -> { "status": "ok" }
- POST /items
- GET /items
- GET /items/{id}
- DELETE /items/{id}
- GET /items/search?q=keyword

## Security Hardening

- Error message, binding details, and stacktrace are not exposed to clients.
- CORS is restricted to localhost development origins.
- Basic response security headers are added (CSP, X-Frame-Options, X-Content-Type-Options, Referrer-Policy).
# sample_java4