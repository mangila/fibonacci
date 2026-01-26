# 🌐 Web Module

The `web` module serves as the primary communication layer of the Fibonacci application. It provides a multi-protocol
interface for clients to interact with the system, including RESTful APIs, Server-Sent Events (SSE), and bi-directional
WebSockets via STOMP.

---

## 🛠 Key Responsibilities

- **API Gateway**: Exposes REST endpoints for initiating new Fibonacci calculations and managing job requests.
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

- **Livestream**: Subscribes to internal `ApplicationEvent` signals (triggered by DB notifications) and broadcasts
  payloads to all active SSE emitters.
- **On-demand Queries**: Facilitates targeted sequence delivery over a persistent channel.

### `WebSocketController`

Implements the STOMP protocol for interactive messaging.

- **Global Topics**: Broadcasts real-time updates to `/topic/livestream`.
- **User Queues**: Delivers personalized query results to private queues (e.g., `/user/queue/fibonacci/list`).

### `PgNotificationListener`

A low-level component that maintains a dedicated JDBC connection to PostgreSQL. It listens for asynchronous `NOTIFY`
signals and converts database events into Spring `ApplicationContext` events for internal distribution.

---

## ⚙️ Operational Flow

1. **Notification**: When the `scheduler` module persists a result, PostgreSQL triggers a `NOTIFY` signal.
2. **Detection**: `PgNotificationListener` intercepts the signal and publishes a `PgNotificationPayloadCollection`
   event.
3. **Broadcast**: Controllers listening for these events process the payload.
4. **Delivery**:
    - `SseController` pushes data to all active `SseEmitter` instances.
    - `WebSocketController` dispatches messages via `SimpMessagingTemplate` to relevant topics.
