package com.github.mangila.fibonacci.web.sse.model;

import io.github.mangila.ensure4j.Ensure;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record Request(
        @NotNull @Valid Subscription subscription,
        @NotNull @Valid Option option
) {
    public Request {
        Ensure.notNull(subscription, "Subscription must not be null");
        Ensure.notNull(option, "Query must not be null");
    }
}
