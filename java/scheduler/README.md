# 📅 Scheduler Module

The `scheduler` module provides robust background processing and task orchestration using **JobRunr**. It is designed to
handle long-running Fibonacci computations asynchronously, ensuring that the system remains responsive even under heavy
computational load.

---

## 🛠 Key Responsibilities

- **Asynchronous Processing**: Offloads intensive `BigInteger` calculations to background workers.
- **Task Orchestration**: Manages complex job lifecycles, including retries, monitoring, and state persistence.
- **Data Integrity**: Implements a dedicated persistence layer to ensure results are stored reliably in PostgreSQL.
- **Operational Visibility**: Integrates the JobRunr Dashboard for real-time monitoring of background tasks.
- **System Synchronization**: Automatically identifies and enqueues missing sequence data on application startup.

---

## Swagger UI

Swagger ui is available at `/swagger-ui/index.html` with spring profile `dev`

---

## 🏗 Architectural Components

### `SchedulerController`

The entry point for computation requests. It validates user input and delegates task scheduling to the
`JobRunrScheduler`.

### `JobRunrScheduler`

The primary entry point for task ingestion. It abstracts the complexity of background scheduling, providing a clean
interface for the `web` module to trigger computations.

### `JobService`

A high-level coordinator annotated with `@Job`. It manages the distribution of work, filtering required sequences and
enqueuing individual atomic computation tasks.

### `TaskService`

Handles the low-level execution logic. It utilizes specialized thread pools (`ThreadPoolTaskExecutor` and
`SimpleAsyncTaskExecutor`) to balance system resources between computational tasks and filtering operations.

### `FibonacciRepository`

The data access layer optimized for PostgreSQL. It handles the persistence of results and queries for sequence
availability using `NamedParameterJdbcTemplate`.

---

## ⚙️ Execution Workflow

1. **Ingestion**: The `client` module submits a calculation range request.
2. **Filtering**: A high-level job scans the database to identify which numbers in the requested range are not yet
   persisted.
3. **Job Spawning**: For every missing value, an atomic computation job is enqueued in the JobRunr backlog.
4. **Computation**: Background workers execute the `core` module's algorithms to calculate the results.
5. **Persistence**: Once calculated, results are saved to PostgreSQL

---

## 📊 Operational Monitoring

The module includes an embedded **JobRunr Dashboard**, accessible during runtime to provide deep insights into the
background worker ecosystem.

- **Dashboard URL**: `http://localhost:8000`
- **Capabilities**: Real-time throughput statistics, worker health monitoring, failure analysis, and manual job retries.
