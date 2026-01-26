package com.github.mangila.fibonacci.web.sse.model;

import jakarta.validation.constraints.Min;

public record SseFibonacciQuery(
        SseSubscription subscription,
        @Min(1) int id
) {
}
