# JobRunr

This module acts as a job scheduler that can produce and consume messages, insert and drain the zset.
It can operate as a producer node, worker node, or both, depending on the configuration. It uses JobRunr as the
underlying job processing framework.

Change config for different modes.

- Producer
- Consumer
- Zset
    - Insert
    - Drain

## Arrow Diagram

```mermaid
graph TD
    subgraph "Redis"
        Queue
        Zset
        Bloomfilter
        Function
    end

    subgraph "JobRunr"
        Consumer
        Producer
        Compute
        Drain-Zset
        Insert-Zset
    end

    DB[(PostgreSQL)]
    Producer <--> Function
    Consumer <--> Queue
    Consumer <--> Bloomfilter
    Compute <--> DB
    Drain-Zset <--> Function
    Drain-Zset <--> DB
    Insert-Zset <--> Zset
    Insert-Zset <--> DB


```
