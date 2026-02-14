package com.github.mangila.fibonacci.web.shared;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;


public record FibonacciStreamOption(@Positive int offset,
                                    @Positive @Max(100) int limit) {
}
