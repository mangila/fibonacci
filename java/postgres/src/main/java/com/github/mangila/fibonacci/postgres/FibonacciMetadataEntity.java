package com.github.mangila.fibonacci.postgres;

import java.time.Instant;

public record FibonacciMetadataEntity(
        int id,
        boolean computed,
        String algorithm,
        Instant updatedAt,
        Instant createdAt
) {
}
