package com.github.mangila.fibonacci.web.ws.model;

import jakarta.validation.constraints.Positive;

public record WsFibonacciStreamQuery(
        @Positive int offset,
        @Positive int limit,
        @Positive int delayInMillis
) {
}
