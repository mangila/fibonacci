package com.github.mangila.fibonacci.web.model;

import io.github.mangila.ensure4j.Ensure;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record FibonacciQuery(
        @Min(0) int offset,
        @Min(1) @Max(1000) int limit
) {
    public FibonacciQuery {
        Ensure.min(0, offset);
        Ensure.positive(limit);
        Ensure.max(1000, limit);
    }
}