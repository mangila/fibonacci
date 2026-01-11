package com.github.mangila.fibonacci.model;

import io.github.mangila.ensure4j.Ensure;

public record FibonacciOption(
        int offset,
        int limit
) {
    public FibonacciOption {
        Ensure.min(1, offset);
        Ensure.min(1, limit);
        Ensure.max(1000, limit);
    }
}