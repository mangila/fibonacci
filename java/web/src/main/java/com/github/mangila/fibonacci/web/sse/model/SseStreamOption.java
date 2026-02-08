package com.github.mangila.fibonacci.web.sse.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;


public record SseStreamOption(@Positive int offset,
                              @Positive @Max(100) int limit) {
}
