# Fibonacci Java

Distributed computing Fibonacci sequence generator created in Java.

### Workflow

Datapipeline is a distributed workflow that generates Fibonacci numbers using JobRunr as a processing engine.

#### Drawbacks with a Distributed workflow

- Operational complexity.
- Stream timeline synchronization. Ensuring that the stream maintains the correct order of Fibonacci sequences is crucial for accurate results.
- Latency and partial failure.
- Race conditions.

#### Pros with a Distributed workflow

- Unlimited scalability.
- High availability.
- Separation of concerns.
- Straightforward to release new versions.

### API

The API exposes the Fibonacci numbers via REST, SSE and WebSockets through STOMP.

### Links

The application is exposing Redis Insight, JobRunr dashboard and a swagger UI.

- [Redis Insight] - (http://localhost:8001/) - Hosted from the docker image `redis/redis-stack`
- [JobRunr dashboard] - (http://localhost:8000/) - Hosted from the web module
- [Swagger UI] - (http://localhost:8080/swagger-ui.html) - Hosted from the web module

### Modules

#### Process modules

- `jobrunr`: jobrunr scheduler service that can act as a producer node, worker node or both. (headless)
- `web`: web api component that exposes the Fibonacci numbers via REST and SSE, uses the Redis stream (web server)

#### Code modules

- `redis` : Everything redis related.
- `redis-test`: Test module for Redis.
- `postgres` : Everything Postgres related.
- `postgres-test`: Test module for Postgres.
- `shared` : Code shared between modules.

## Arrow Diagram

```mermaid
graph TD
    subgraph "Backend"
        Web[Web]
        Scheduler[JobRunr]
        Scheduler[JobRunr]
        DB[(PostgreSQL)]
        DB2[(Redis)]
    end

    subgraph "Frontend"
        Client[Frontend]
    end

    Scheduler <--> DB
    Scheduler <--> DB2
    Web <--> DB
    Web <--> DB2
    Web <--> Client


```


