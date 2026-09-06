# Banking Management System — Spring Boot Backend

The JDBC business logic has been migrated to a Spring Boot REST API. The backend now uses controller, service, and Spring Data JPA repository layers. Credit, debit, and transfer operations run inside database transactions, and concurrent updates lock the affected accounts.

## Requirements

- Java 17 or newer
- MySQL 8

## Configuration

The app reads database settings from environment variables. Defaults are shown below; set `DB_PASSWORD` before starting if your MySQL user has a password.

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/banking_system?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-password"
```

Spring/JPA creates or updates the schema on startup. Existing users and accounts remain usable. When an old plain-text password or PIN is used successfully, the service replaces it with a BCrypt hash.

## Run

```powershell
.\mvnw.cmd spring-boot:run
```

The API starts at `http://localhost:8080`.

## API

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Register a user |
| POST | `/api/auth/login` | Log in |
| POST | `/api/accounts` | Open an account |
| GET | `/api/accounts?email=user@example.com` | Find a user's account |
| GET | `/api/accounts/{accountNumber}` | Get account details |
| POST | `/api/accounts/{accountNumber}/credit` | Credit money |
| POST | `/api/accounts/{accountNumber}/debit` | Debit money |
| POST | `/api/accounts/{accountNumber}/transfer` | Transfer money |
| POST | `/api/accounts/{accountNumber}/balance` | Check balance using a PIN |

Example registration:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/auth/register `
  -ContentType application/json `
  -Body '{"name":"Abhishek","email":"abhishek@example.com","password":"secret1"}'
```

Example account creation:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/accounts `
  -ContentType application/json `
  -Body '{"fullName":"Abhishek","email":"abhishek@example.com","initialBalance":1000,"pin":"1234"}'
```

Example transfer:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/accounts/10000100/transfer `
  -ContentType application/json `
  -Body '{"receiverAccountNumber":10000101,"amount":250,"pin":"1234"}'
```

## Test and package

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
java -jar target/banking-system-1.0.0.jar
```

The original console/Swing JDBC source is retained under `src/BankingManagementSystem` as a reference during migration. The Spring Boot backend is under `src/main/java` and is the maintained application.
