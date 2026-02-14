package com.github.mangila.fibonacci.web.shared;

import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.ops.EnsureNumberOps;
import jakarta.validation.constraints.Positive;

public record FibonacciIdOption(@Positive int id) {

    private static final EnsureNumberOps ENSURE_NUMBER_OPS = Ensure.numbers();

    public FibonacciIdOption {
        ENSURE_NUMBER_OPS.positive(id);
    }
}
