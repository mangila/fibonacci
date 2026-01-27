package com.github.mangila.fibonacci.web.dto;

import jakarta.validation.constraints.Positive;

public record SseFibonacciStreamQuery(
        SseSubscription subscription,
        @Positive int offset,
        @Positive int limit
) {
}
