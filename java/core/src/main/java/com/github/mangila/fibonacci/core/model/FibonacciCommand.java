package com.github.mangila.fibonacci.core.model;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FibonacciCommand(@NotNull FibonacciAlgorithm algorithm,
                               @Min(1) @Max(1_000_000) int offset,
                               @Min(1) @Max(1000) int limit,
                               @Min(1) @Max(10000) int delayInMillis) {
}