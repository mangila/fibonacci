# 🌀 Fibonacci

A full-stack application for calculating, persisting, and streaming Fibonacci sequences in real-time. This project demonstrates high-performance computing, distributed task scheduling, and reactive data streaming across different architectural layers.

## 🚀 Overview

The Fibonacci project is designed to handle massive Fibonacci computations efficiently. It leverages a producer-consumer pattern where heavy computations are offloaded to background workers, and results are streamed back to the client in real-time.

### 🌐 Server Implementations

Currently, the project features a robust Java implementation. More languages may be added in the future to compare performance and developer experience.

- **[Java Server](./java)**: Built with Java 25 (Virtual Threads), Spring Boot 4, and JobRunr.

### 💻 Client

The frontend is a modern dashboard that visualizes the Fibonacci stream and allows users to trigger new calculations.

- **[Web Client](./client)**: Built with Astro 5, React 19, and Tailwind CSS 4.

## 🏗 High-Level Architecture

The system follows a reactive architecture:

1.  **Client** requests a Fibonacci sequence range via REST.
2.  **Server** acknowledges the request and enqueues background jobs.
3.  **Workers** calculate the numbers using various algorithms (Iterative, Fast Doubling, etc.).
4.  **Database** persists the results.
5.  **Streaming Layer** detects new results (via DB notifications) and pushes them to the **Client** using **Server-Sent Events (SSE)** or **WebSockets**.

---

## 🏁 Getting Started

To get the entire system up and running, follow these steps:

### 1. Prerequisites
- Docker & Docker Compose
- Java 25 (for Java backend)
- Node.js 22 (for Web client)

### 2. Infrastructure
Start the PostgreSQL database:
```bash
docker-compose up -d
```

### 3. Run the Backend (Java)
```bash
cd java
./mvnw clean install
./mvnw -pl web spring-boot:run
```

### 4. Run the Client
```bash
cd client
npm install
npm run dev
```

Visit `http://localhost:4321` to view the dashboard.

---

> [!NOTE]
> This project is a technical showcase for modern software engineering practices, including Project Loom (Virtual Threads), reactive streaming, and distributed task management.
