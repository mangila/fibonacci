package com.github.mangila.fibonacci.model;

import java.math.BigDecimal;

public record FibonacciResult(int sequence, BigDecimal result, int precision) {

    public static FibonacciResult of(int sequence, BigDecimal bigDecimal) {
        return new FibonacciResult(sequence, bigDecimal, bigDecimal.precision());
    }
}
