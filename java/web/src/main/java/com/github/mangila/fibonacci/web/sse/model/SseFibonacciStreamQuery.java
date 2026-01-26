package com.github.mangila.fibonacci.web.sse.model;

import jakarta.validation.constraints.Positive;

public record SseFibonacciStreamQuery(
        SseSubscription subscription,
        @Positive int offset,
        @Positive int limit,
        @Positive int delayInMillis
) {
}
