package com.github.mangila.fibonacci.core.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record FibonacciQuery(
        @Min(0) @Max(1_000_000) int offset,
        @Min(1) @Max(1000) int limit
) {
}