package com.github.mangila.fibonacci.postgres;

import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.ops.EnsureNumberOps;

import java.math.BigDecimal;

public record FibonacciEntity(int id,
                              int sequence,
                              BigDecimal result,
                              int precision) {

    private static final EnsureNumberOps ENSURE_NUMBER_OPS = Ensure.numbers();

    public FibonacciEntity {
        ENSURE_NUMBER_OPS.positive(id);
        ENSURE_NUMBER_OPS.positive(precision);
        Ensure.notNull(result);
        ENSURE_NUMBER_OPS.positive(sequence);
    }
}
