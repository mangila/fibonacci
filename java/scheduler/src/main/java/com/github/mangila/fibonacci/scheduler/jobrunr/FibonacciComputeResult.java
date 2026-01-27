package com.github.mangila.fibonacci.scheduler.jobrunr;

import io.github.mangila.ensure4j.Ensure;

import java.math.BigDecimal;

public record FibonacciComputeResult(int sequence, BigDecimal result, int precision) {

    public FibonacciComputeResult {
        Ensure.positive(sequence);
        Ensure.notNull(result);
        Ensure.positive(precision);
    }

    public static FibonacciComputeResult of(int sequence, BigDecimal bigDecimal) {
        return new FibonacciComputeResult(sequence, bigDecimal, bigDecimal.precision());
    }
}
