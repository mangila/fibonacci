package com.github.mangila.fibonacci.postgres;

import io.github.mangila.ensure4j.Ensure;

import java.math.BigDecimal;

public record FibonacciEntity(int id,
                              int sequence,
                              BigDecimal result,
                              int precision) {
    public FibonacciEntity {
        Ensure.positive(id);
        Ensure.positive(precision);
        Ensure.notNull(result);
        Ensure.positive(sequence);
    }
}
