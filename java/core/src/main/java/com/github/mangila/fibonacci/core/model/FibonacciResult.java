package com.github.mangila.fibonacci.core.model;

import io.github.mangila.ensure4j.Ensure;

import java.math.BigDecimal;

public record FibonacciResult(int sequence, BigDecimal result, int precision) {

    public FibonacciResult {
        Ensure.positive(sequence);
        Ensure.notNull(result);
        Ensure.positive(precision);
    }

    public static FibonacciResult of(int sequence, BigDecimal bigDecimal) {
        return new FibonacciResult(sequence, bigDecimal, bigDecimal.precision());
    }
}
