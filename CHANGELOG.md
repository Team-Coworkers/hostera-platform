# Changelog

All notable changes to this project are documented in this file.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project
adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.3.0] - 2026-11-14

### Added

- Rooms bounded context: room types, rooms, rate plans, daily rates and status periods, with
  unique room numbers per property, positive rates and valid date ranges, the room status and
  the RoomsContextFacade for the other contexts.
- Inventory bounded context: inventory items with their stocks and adjustments, and storage
  locations with unique codes per property, without negative stock.
- Demonstration seed of both contexts with the identifiers of the frontend data.

## [0.2.0] - 2026-11-14

### Added

- Bookings bounded context: bookings and payments with CRUD and filters, rejection of a second
  active booking of the same room for overlapping nights, and the demonstration seed.
- Access control bounded context: key-card credentials and access events; a credential is only
  issued for the room of a checked-in booking, checking out or cancelling a booking revokes its
  key cards, and access attempts with an inactive credential or at another room are denied.
- GitHub Actions workflow that runs the tests on every push.

## [0.1.0] - 2026-11-14

### Added

- Maven project with Spring Boot 4.1, Java 25, PostgreSQL and the `dev`, `prod` and `test` profiles.
- Shared kernel: `Result`, `ApplicationError`, Problem Details error handling, i18n (en/es),
  OpenAPI configuration, snake_case pluralized table names and auditing.
- IAM bounded context: sign-up, sign-in with JWT, users and roles.
- Organizations bounded context: properties and staff members with CRUD, filters by
  `organizationId` and `propertyId`, OpenAPI documentation and the demonstration seed.
- Multi-stage `Dockerfile` for the deployment.

[Unreleased]: https://github.com/Team-Coworkers/hostera-platform/compare/v0.3.0...develop
[0.3.0]: https://github.com/Team-Coworkers/hostera-platform/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/Team-Coworkers/hostera-platform/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/Team-Coworkers/hostera-platform/releases/tag/v0.1.0
