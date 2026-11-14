# Changelog

All notable changes to this project are documented in this file.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project
adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Maven project with Spring Boot 4.1, Java 25, PostgreSQL and the `dev`, `prod` and `test` profiles.
- Shared kernel: `Result`, `ApplicationError`, Problem Details error handling, i18n (en/es),
  OpenAPI configuration, snake_case pluralized table names and auditing.
- IAM bounded context: sign-up, sign-in with JWT, users and roles.
- Multi-stage `Dockerfile` for the deployment.
