package com.github.mangila.fibonacci.core.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record FibonacciQuery(
        @Positive @Max(10_000) int delay,
        @PositiveOrZero @Max(1_000_000) int offset,
        @Positive @Max(1000) int limit
) {
}