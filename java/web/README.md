# 🌐 Web Module

The `web` module serves as the primary communication layer of the Fibonacci application. It provides a multi-protocol
interface for clients to interact with the system, including RESTful APIs, Server-Sent Events (SSE), and bi-directional
WebSockets via STOMP.

---

## 🛠 Key Responsibilities

- **Reactive Streaming**:
    - **SSE**: Unidirectional, high-efficiency streaming of results to web clients.
    - **WebSockets**: Bi-directional communication using STOMP for complex client interactions.
- **Request Validation**: Enforces strict constraints on incoming requests to ensure system stability.

---

## Swagger UI

Swagger ui is available at `/swagger-ui/index.html` with spring profile `dev`

---

## 🏗 Core Components

### `SseController`

Manages persistent Server-Sent Events connections.

- **On-demand Queries**: Facilitates targeted sequence delivery over a persistent channel.
- **On-demand Stream**: Facilitates targeted sequences delivery over a persistent channel.

### `WebSocketController`

Implements the STOMP protocol for interactive messaging.

- **User Queues**: Delivers personalized query results to private queues 
  - `/user/queue/fibonacci/stream`
  - `/user/queue/fibonacci/id`

---

