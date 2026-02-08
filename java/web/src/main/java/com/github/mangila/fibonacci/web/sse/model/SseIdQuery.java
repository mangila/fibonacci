package com.github.mangila.fibonacci.web.sse.model;

import io.github.mangila.ensure4j.Ensure;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SseIdQuery(@NotNull @Valid SseSubscription sseSubscription,
                         @NotNull @Valid SseIdOption option) {
    public SseIdQuery {
        Ensure.notNull(sseSubscription);
        Ensure.notNull(option);
    }
}
