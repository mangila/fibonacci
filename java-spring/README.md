# Fibonacci Java Spring

A reactive and highly scalable Spring Boot application that computes Fibonacci numbers and streams updates in real-time using modern Java features and efficient communication protocols.

## Features

- **Fibonacci Generation**: Automatically computes Fibonacci numbers and persists them to a PostgreSQL database.
- **Real-time Streaming**:
    - **WebSockets (STOMP)**: Two-way communication for querying results and receiving live updates.
    - **Server-Sent Events (SSE)**: One-way streaming of new results and on-demand queries.
- **PostgreSQL Notifications**: Uses `LISTEN/NOTIFY` via `pg_notify` to trigger application events whenever a new Fibonacci number is inserted.
- **Modern Java**: Built with **Java 25**, leveraging **Virtual Threads** (Project Loom) for high-concurrency I/O operations.
- **Scalable Architecture**: Decouples computation (Platform Threads) from I/O (Virtual Threads) for optimal resource utilization.

## Tech Stack

- **Java 25**
- **Spring Boot 4.0.1**
- **Spring Web** & **Spring WebSocket**
- **Spring JDBC**
- **PostgreSQL 18**
- **Maven**
- **Testcontainers** (for integration testing)
- **Docker Compose** (for local development)

## Getting Started

### Prerequisites

- JDK 25
- Docker & Docker Compose
- Maven 3.9+

### Running the Application

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd fibonacci/java-spring
   ```

2. **Start the database**:
   The application uses `spring-boot-docker-compose` to automatically start PostgreSQL. Ensure Docker is running.

3. **Build and Run**:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start at `http://localhost:8080`.

## API Documentation

### Server-Sent Events (SSE)

- **Subscribe**: `GET /api/v1/sse/fibonacci/subscribe/{username}`
- **Subscribe to Livestream**: `GET /api/v1/sse/fibonacci/subscribe/livestream/{username}`
- **Unsubscribe**: `DELETE /api/v1/sse/fibonacci/subscribe/{username}`
- **Unsubscribe from Livestream**: `DELETE /api/v1/sse/fibonacci/subscribe/livestream/{username}`
- **Query List**: `POST /api/v1/sse/fibonacci/{username}`
    - Body: `{"offset": 0, "limit": 100}`
- **Query by ID**: `GET /api/v1/sse/fibonacci/{username}?id={id}`

### WebSockets (STOMP)

- **Endpoint**: `/ws`
- **Topic (Livestream)**: `/topic/livestream`
- **User Queue (Results)**: `/user/queue/fibonacci`
- **User Queue (Single Result)**: `/user/queue/fibonacci/id`
- **User Queue (Errors)**: `/user/queue/errors`
- **Message Mappings**:
    - `fibonacci`: Send `FibonacciOption` to receive a list of results.
    - `fibonacci/id`: Send an `int` ID to receive a specific result.

## Database Schema

The application uses a PostgreSQL table `fibonacci_results` and a trigger function `notify_new_fibonacci_result_fn` to notify the application of new inserts.

```sql
CREATE TABLE IF NOT EXISTS fibonacci_results (
    id        SERIAL PRIMARY KEY,
    result    NUMERIC NOT NULL,
    precision INT     NOT NULL
);
```

## Configuration

Configuration can be found in `src/main/resources/application.yaml`. It includes datasource settings and Spring Boot Docker Compose configurations.
