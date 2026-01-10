package com.github.mangila.fibonacci.model;

import io.github.mangila.ensure4j.Ensure;

public record FibonacciOption(
        long offset,
        int limit
) {
    public FibonacciOption {
        Ensure.min(1, (int) offset);
        Ensure.min(1, limit);
        Ensure.max(100_000, limit);
    }
}