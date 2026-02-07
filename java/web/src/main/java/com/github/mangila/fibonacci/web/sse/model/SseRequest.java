package com.github.mangila.fibonacci.web.sse.model;

import io.github.mangila.ensure4j.Ensure;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SseRequest(
        @NotNull @Valid SseSubscription sseSubscription,
        @NotNull @Valid SseOption option
) {
    public SseRequest {
        Ensure.notNull(sseSubscription, "Subscription must not be null");
        Ensure.notNull(option, "Query must not be null");
    }
}
