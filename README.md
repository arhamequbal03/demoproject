# Library Management System

A RESTful backend for managing a library — books, members, and borrow/return
records — built with **Spring Boot 4** and **PostgreSQL**. Authentication is
done via HTTP **Basic Auth**, and access is gated by two roles: `ADMIN` and
`MEMBER`.

## Tech Stack

- Java 17
- Spring Boot 4.1 (Spring Web MVC, Spring Data JPA)
- PostgreSQL
- Maven (with the bundled Maven Wrapper)

## Features

- **Books** — list, fetch, add, edit, and delete books (admin-only writes),
  with quantity/availability tracking.
- **Members** — user accounts with `ADMIN`/`MEMBER` roles, login, and admin
  management (create/update/delete).
- **Reports** — issue and return books, per-member borrow limits, and automatic
  fine calculation for late returns.

## Project Structure

```
src/main/java/com/arham/demo_project/
├── controllers/     # REST endpoints (book, user, report)
├── services/        # Business logic
├── repositry/       # Spring Data JPA repositories
├── model/           # JPA entities (book, member, report) + userObject
└── GlobalExceptionHandler.java
```

## Getting Started

### Prerequisites

- JDK 17+
- A running PostgreSQL instance with a database (default name: `library_db`)

### Configuration

Database credentials are **not** stored in the repository. They are read from
environment variables. Copy the example file and set your own values:

```bash
cp .env.example .env
# then edit .env, or export the variables in your shell:
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password
export DB_URL=jdbc:postgresql://localhost:5432/library_db
```

| Variable      | Description                       | Default                                       |
| ------------- | --------------------------------- | --------------------------------------------- |
| `DB_USERNAME` | PostgreSQL username               | _(required)_                                  |
| `DB_PASSWORD` | PostgreSQL password               | _(required)_                                  |
| `DB_URL`      | JDBC connection URL               | `jdbc:postgresql://localhost:5432/library_db` |

The schema is created/updated automatically on startup
(`spring.jpa.hibernate.ddl-auto=update`).

### Run

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`.

## Authentication

Every endpoint (except `/getuser`) expects an `Authorization: Basic <token>`
header, where the token is the Base64 encoding of `username:password` for an
existing member. The role attached to that member (`ADMIN` or `MEMBER`)
determines what actions are permitted.

## API Reference

Base path: `/v1/library`

### Users / Members

| Method | Endpoint      | Role        | Description                          |
| ------ | ------------- | ----------- | ------------------------------------ |
| GET    | `/getuser`    | —           | List all members                     |
| POST   | `/login`      | Any member  | Validate credentials, return profile |
| POST   | `/users`      | ADMIN       | Add a new member                     |
| PUT    | `/users/{id}` | ADMIN       | Update a member                      |
| DELETE | `/users/{id}` | ADMIN       | Delete a member                      |

### Books

| Method | Endpoint      | Role        | Description        |
| ------ | ------------- | ----------- | ------------------ |
| GET    | `/books`      | Any member  | List all books     |
| GET    | `/books/{id}` | Any member  | Get a book by id   |
| POST   | `/books`      | ADMIN       | Add a book         |
| PUT    | `/books/{id}` | ADMIN       | Edit a book        |
| DELETE | `/books/{id}` | ADMIN       | Delete a book      |

### Reports (Issue / Return)

| Method | Endpoint                 | Role       | Description                              |
| ------ | ------------------------ | ---------- | ---------------------------------------- |
| GET    | `/reports`               | ADMIN      | View all borrow records                  |
| GET    | `/reports/{userid}`      | ADMIN/self | Books currently/previously issued to user|
| GET    | `/reports/v1/{book_id}`  | ADMIN      | Borrow history for a specific book       |
| POST   | `/reports`               | ADMIN      | Issue a book to a member                 |
| POST   | `/reports/{id}`          | ADMIN      | Return a book (calculates fine)          |

### Business Rules

- A member can hold at most **2** books at a time.
- A book can only be issued while `total_quantity > issue_quantity`.
- On return, a fine of **₹2 per day** is applied for each day beyond a **14-day**
  borrowing window; the fine is added to the member's total dues.

## Error Handling

All errors are returned as a consistent JSON body via `GlobalExceptionHandler`:

```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Not Found",
  "message": "Book not found"
}
```