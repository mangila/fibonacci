package com.github.mangila.fibonacci.postgres;

import java.time.Instant;

public record FibonacciMetadataEntity(
        int id,
        boolean sentToZset,
        boolean sentToStream,
        Instant updatedAt,
        Instant createdAt
) {
}
