package com.github.mangila.fibonacci.model;

import java.math.BigDecimal;

public record FibonacciResult(BigDecimal result, int precision) {

    public static FibonacciResult of(BigDecimal bigDecimal) {
        return new FibonacciResult(bigDecimal, bigDecimal.precision());
    }
}
