package com.github.mangila.fibonacci.web.sse.model;

import io.github.mangila.ensure4j.Ensure;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SseStreamQuery(
        @NotNull @Valid SseSubscription sseSubscription,
        @NotNull @Valid SseStreamOption option
) {
    public SseStreamQuery {
        Ensure.notNull(sseSubscription, "Subscription must not be null");
        Ensure.notNull(option, "Option must not be null");
    }
}
