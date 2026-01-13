# 🔢 Fibonacci Java Spring

Spring Boot application that computes Fibonacci numbers and streams updates in real-time.
Built with **Java 25**, it leverages **Virtual Threads** (Project Loom) and modern communication protocols to provide a
robust and efficient experience.

---

## 🚀 Key Features

- **Fibonacci Generation**: Automatically computes Fibonacci numbers and persists them to a PostgreSQL database.
- **Real-time Streaming**:
    - **WebSockets (STOMP)**: Two-way communication for querying results and receiving live updates.
    - **Server-Sent Events (SSE)**: Efficient one-way streaming of new results and on-demand queries.
- **Advanced Concurrency**: Uses **Project Loom** (Virtual Threads) for I/O-bound tasks and **Platform Threads** for
  CPU-intensive computations.
- **PostgreSQL Notifications**: Utilizes `LISTEN/NOTIFY` via `pg_notify` to trigger application events whenever a new
  Fibonacci number is inserted, ensuring near-instant updates.
- **Flexible Algorithms**: Choose between several computation methods via configuration.

---

## 🛠 Tech Stack

- **Java 25** (with Virtual Threads enabled)
- **Spring Boot 4.0.1**
- **Spring Web** & **Spring WebSocket**
- **Spring JDBC**
- **PostgreSQL 18**
- **Maven**
- **Testcontainers** (for seamless integration testing)
- **Docker Compose** (for local development environments)

---

## 🏁 Getting Started

### Prerequisites

- **JDK 25**
- **Docker & Docker Compose**
- **Maven 3.9+**

### Running the Application

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd fibonacci/java-spring
   ```

2. **Start the database**:
   The application uses `spring-boot-docker-compose` to automatically spin up a PostgreSQL instance. Just make sure
   Docker is running.

3. **Build and Run**:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will be accessible at `http://localhost:8080`.

---

## 📖 API Documentation

### Server-Sent Events (SSE)

| Endpoint                                                |  Method  | Description                                                   |
|:--------------------------------------------------------|:--------:|:--------------------------------------------------------------|
| `/api/v1/sse/fibonacci/subscribe/{username}`            |  `GET`   | Subscribe to standard updates                                 |
| `/api/v1/sse/fibonacci/subscribe/livestream/{username}` |  `GET`   | Subscribe to the live computation stream                      |
| `/api/v1/sse/fibonacci/subscribe/{username}`            | `DELETE` | Unsubscribe from standard updates                             |
| `/api/v1/sse/fibonacci/subscribe/livestream/{username}` | `DELETE` | Unsubscribe from the livestream                               |
| `/api/v1/sse/fibonacci/{username}`                      |  `POST`  | Query a list of results (Body: `{"offset": 0, "limit": 100}`) |
| `/api/v1/sse/fibonacci/{username}?id={id}`              |  `GET`   | Query a specific result by ID                                 |

### WebSockets (STOMP)

- **Endpoint**: `/ws`
- **Topic (Livestream)**: `/topic/livestream`
- **User Queue (Results)**: `/user/queue/fibonacci`
- **User Queue (Single Result)**: `/user/queue/fibonacci/id`
- **User Queue (Errors)**: `/user/queue/errors`
- **Message Mappings**:
    - `fibonacci`: Send `FibonacciOption` to receive a list of results.
    - `fibonacci/id`: Send an `int` ID to receive a specific result.

---

## ⚙️ Configuration

Configuration is managed via `src/main/resources/application.yaml`.

| Property                  | Default     | Description                                                 |
|:--------------------------|:------------|:------------------------------------------------------------|
| `app.fibonacci.algorithm` | `ITERATIVE` | Algorithm to use: `ITERATIVE`, `RECURSIVE`, `FAST_DOUBLING` |
| `app.fibonacci.offset`    | `1`         | Start Fibonacci index to compute                            |
| `app.fibonacci.limit`     | `1000`      | Maximum Fibonacci index to compute                          |
| `app.fibonacci.delay`     | `1s`        | Delay between computation tasks                             |

---

## 🗄 Database Schema

The application uses a PostgreSQL table `fibonacci_results` and a trigger function `notify_new_fibonacci_result_fn` to
notify the application of new inserts.

---

Built with ❤️ by Junie
