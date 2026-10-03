# CareerHub Backend

CareerHub Backend is a Java 21 and Spring Boot REST API for career and job-application management.

## Features

- JWT registration, login, refresh, and logout
- Job and application tracking, search, and status transitions
- Resume and user profile management
- Dashboard summaries, trends, and recent activity
- Notifications, preferences, and audit history
- File uploads and presigned URLs through AWS S3
- Kafka-based event processing for notifications and audits

## Requirements

- Java 21
- Docker with the Compose plugin
- AWS credentials and configuration for AWS-backed features

## Local setup

1. Create a local environment file from the template if you do not already have one:

   ```bash
   cp backend.env.template backend.env
   ```

   Fill in the values in `backend.env`. This file is git-ignored; do not commit secrets.
2. Start the supporting services using the Compose configuration in `../careerhub-infra`. Configure its required local environment file as described in that project's setup.
3. Load the backend environment values into your shell and run the application:

   ```bash
   set -a
   source backend.env
   set +a
   ./mvnw spring-boot:run
   ```

The API listens on port `8000` by default. Health information is available at `http://localhost:8000/actuator/health`.

## Configuration

`backend.env.template` lists the environment variables used by the application:

- MySQL: `MYSQL_URL`, `MYSQL_USERNAME`, `MYSQL_PASSWORD`
- MongoDB: `MONGODB_URI`
- Redis: `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`, `REDIS_TIMEOUT`
- Kafka: `KAFKA_BOOTSTRAP_SERVERS`, `KAFKA_CONSUMER_GROUP`
- Authentication: `JWT_SECRET`, `ADMIN_EMAIL`, `ADMIN_PASSWORD`
- AWS: `AWS_REGION`, `AWS_S3_FILES_BUCKET`, `AWS_S3_WATERMARK_FILES_BUCKET`, `AWS_S3_PROFILES_BUCKET`, `AWS_CLOUD_FRONT_URL`, `AWS_SES_FROM_EMAIL`, `AWS_SECRET`, `AWS_HASHING_ALGORITHM`

See `src/main/resources/application.properties` for how these values are used and for optional defaults.

## Build and test

```bash
./mvnw clean verify
```

Integration tests use Testcontainers and may require Docker.

## API routes

Routes use the `/api/v1` prefix. Main areas include `/auth`, `/users`, `/jobs`, `/applications`, `/resumes`, `/dashboard`, `/notifications`, `/audit`, and `/files`.
