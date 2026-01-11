package com.github.mangila.fibonacci.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record FibonacciOption(
        @Min(0) int offset,
        @Min(1) @Max(1000) int limit
) {
}