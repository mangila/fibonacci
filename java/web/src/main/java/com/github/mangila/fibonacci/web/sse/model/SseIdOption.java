package com.github.mangila.fibonacci.web.sse.model;

import io.github.mangila.ensure4j.Ensure;
import jakarta.validation.constraints.Positive;

public record SseIdOption(@Positive int id) {

    public SseIdOption {
        Ensure.positive(id);
    }
}
