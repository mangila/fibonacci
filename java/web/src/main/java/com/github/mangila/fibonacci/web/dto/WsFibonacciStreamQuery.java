package com.github.mangila.fibonacci.web.dto;

import jakarta.validation.constraints.Positive;

public record WsFibonacciStreamQuery(
        @Positive int offset,
        @Positive int limit,
        @Positive int delayInMillis
) {
}
