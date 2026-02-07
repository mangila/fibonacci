package com.github.mangila.fibonacci.web.sse.model;

import io.github.mangila.ensure4j.Ensure;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SseQueryRequest(
        @NotNull @Valid Subscription subscription,
        @NotNull @Valid Query query
) {
    public SseQueryRequest {
        Ensure.notNull(subscription, "Subscription must not be null");
        Ensure.notNull(query, "Query must not be null");
    }
}
