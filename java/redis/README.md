# Redis module

This module contains Redis-specific functionality.

### Redis functions

In the resources folder you can find the Redis functions (LUA scripts).

Redis Fuctions is well suited for scenarios when Read/Transform/Write operations are required.

Instead of going back and forth between Redis Server and the redis client.
All the redis commands are executed on the server.
Combine function calls with Redis pipelines for better performance.