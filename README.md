# Task Manager API

REST API for managing personal tasks with user authentication and ownership control.

## Stack

Java 17 • Spring Boot 3.5 • Spring Security • Spring Data JDBC • H2 Database • Gradle • JUnit 5

## Features

- CRUD operations on tasks
- HTTP Basic authentication
- Tasks are private to each user
- Pagination and sorting support
- 22 integration and JSON tests
- In-memory user database

## Endpoints

```
GET  /tasks/{id}                      Get a task by ID
GET  /tasks?page=0&size=10&sort=id    List paginated tasks
POST /tasks                            Create a new task
PUT  /tasks/{id}                       Update a task
DELETE /tasks/{id}                     Delete a task
```

## Data Model

| Field | Type | Notes |
|-------|------|-------|
| id | Long | Auto-generated |
| title | String | Required |
| description | String | Optional |
| completed | Boolean | |
| owner | String | Username of creator |

## Authentication

**Method:** HTTP Basic Auth

**Required Role:** TASK-OWNER

**Demo Users:**

| Username | Password | Role |
|----------|----------|------|
| sergio | abc123 | TASK-OWNER |
| david | xyz789 | TASK-OWNER |
| maria-owns-no-tasks | qrs456 | NON-OWNER |

## Examples

Get a task:
```bash
curl -u sergio:abc123 http://localhost:8080/tasks/99
```

Create a task:
```bash
curl -u sergio:abc123 -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Buy groceries","description":"Milk, eggs, bread","completed":false}'
```

Update a task:
```bash
curl -u sergio:abc123 -X PUT http://localhost:8080/tasks/99 \
  -H "Content-Type: application/json" \
  -d '{"title":"Buy groceries","description":"Milk, eggs, bread, cheese","completed":true}'
```

Delete a task:
```bash
curl -u sergio:abc123 -X DELETE http://localhost:8080/tasks/99
```

List tasks (page 0, 5 items per page, sorted by id descending):
```bash
curl -u sergio:abc123 http://localhost:8080/tasks?page=0&size=5&sort=id,desc
```

## Running

Start the server:
```bash
./gradlew bootRun
```

Run tests:
```bash
./gradlew test
```

Server runs on `http://localhost:8080`

## Project Structure

```
src/main/java/com/sergio/taskmanager/
├── Task.java                    (entity)
├── TaskController.java          (REST endpoints)
├── TaskRepository.java          (database access)
├── SecurityConfig.java          (authentication/authorization)
└── TaskmanagerApplication.java

src/main/resources/
├── schema.sql                   (database schema)
└── application.properties

src/test/
├── TaskmanagerApplicationTests.java  (integration tests)
└── TaskJsonTest.java                 (serialization tests)
```

## Security Notes

- Users can only access their own tasks
- Attempting to access another user's task returns 404
- Users without TASK-OWNER role receive 403 Forbidden
- Invalid credentials return 401 Unauthorized