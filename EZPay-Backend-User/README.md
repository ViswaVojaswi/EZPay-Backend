# EzPay — Use Case 1: User Management

Spring Boot 3.4.5, Java 21, Oracle Database, Spring Data JPA, Log4j2 and JUnit 5.

## Included
- User registration
- Login using email or mobile number
- Profile retrieval and updates
- Password-reset demonstration
- Three application layers: entity, repository, service, controller
- Log4j2 console and rolling-file logging
- JUnit 5 integration tests using real Spring beans (no Mockito)
- Oracle schema script

## Excluded
Use Case 4 (Notification System), Use Case 5 (Security and Compliance as a separate use case), payment processing and transaction management.

## Setup
1. Create/open an Oracle schema in SQL Developer.
2. Run `database/01_create_ezpay_users.sql`.
3. Edit `src/main/resources/application.properties` and replace the sample Oracle URL, username and password.
4. Import the folder into Eclipse as an existing Maven project.
5. Right-click project → Maven → Update Project.
6. Run `EzPayApplication.java`.
7. Run `UserServiceIntegrationTest.java` as JUnit Test. The tests require an accessible Oracle database and the table above.

## API endpoints
Base URL: `http://localhost:8080/api/users`

- `POST /register?password=...` — JSON body contains fullName, email, mobileNumber, address
- `POST /login?loginId=...&password=...`
- `GET /{userId}`
- `PUT /{userId}` — JSON body contains fullName, email, mobileNumber, address
- `POST /password-reset/request?loginId=...`
- `POST /password-reset/confirm?resetToken=...&newPassword=...`

## Important security limitations
This is a learning/demo scaffold, not production-ready payment software.
- Password query parameters are used only to honor the no-DTO constraint; URLs can be logged. Prefer a request model and POST body for production.
- The reset endpoint returns a reset token only for local demonstration. Deliver it through a verified channel and never expose it in API responses or logs in production.
- Login verifies credentials but does not establish an authenticated session or issue a token.
- Profile routes are not protected by authentication/authorization.
- Use a dedicated test schema; never run tests against production data.
