package com.github.mangila.fibonacci.postgres;

import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.ops.EnsureNumberOps;

public record FibonacciProjection(int id,
                                  int sequence,
                                  int precision) {

    private static final EnsureNumberOps ENSURE_NUMBER_OPS = Ensure.numbers();

    public FibonacciProjection {
        ENSURE_NUMBER_OPS.positive(id);
        ENSURE_NUMBER_OPS.positive(sequence);
        ENSURE_NUMBER_OPS.positive(precision);
    }
}
