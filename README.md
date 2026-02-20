> [!NOTE]
> This project is a technical showcase for modern software engineering practices and distributed task management.

# 🌀 Fibonacci

Generates fibonacci sequences with different programming languages and display in the client using SSE and WebSockets

## 🚀 Overview

The Fibonacci project is designed to handle massive Fibonacci computations efficiently. It leverages a producer-consumer pattern where heavy computations are offloaded to background workers, and results are livestreamed back to the client using Postgres LISTEN/NOTIFY functionality.

### 🌐 Server Implementations

Currently, the project features a robust Java implementation. More languages may be added in the future to compare performance and developer experience.

- **[Java Server](./java)**: Built with Java 25 (Virtual Threads), Spring Boot 4, and JobRunr.

### 💻 Client

The frontend is a modern dashboard that visualizes the Fibonacci stream and allows users to trigger new calculations.

- **[Web Client](./client)**: Built with Astro 5, React 19, and Tailwind CSS 4.
