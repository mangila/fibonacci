package com.github.mangila.fibonacci.core.model;

import io.github.mangila.ensure4j.Ensure;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record FibonacciQuery(
        @Min(0) @Max(1_000_000) int offset,
        @Min(1) @Max(1000) int limit
) {
    public FibonacciQuery {
        Ensure.min(0, offset);
        Ensure.positive(limit);
        Ensure.max(1000, limit);
    }
}