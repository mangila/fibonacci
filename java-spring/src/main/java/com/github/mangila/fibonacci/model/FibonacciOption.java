package com.github.mangila.fibonacci.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record FibonacciOption(
        @Min(1) int offset,
        @Min(1) @Max(100_000) int limit
) {
}