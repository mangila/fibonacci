package com.github.mangila.fibonacci.web.dto;

import jakarta.validation.constraints.Min;

public record SseFibonacciQuery(
        SseSubscription subscription,
        @Min(1) int id
) {
}
