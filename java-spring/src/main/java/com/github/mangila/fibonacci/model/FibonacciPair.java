package com.github.mangila.fibonacci.model;

import java.math.BigDecimal;

public record FibonacciPair(FibonacciResult previous, FibonacciResult current) {

    public static final FibonacciPair DEFAULT = new FibonacciPair(
            FibonacciResult.of(0, BigDecimal.ZERO),
            FibonacciResult.of(1, BigDecimal.ONE)
    );

    public boolean isDefault() {
        return this == DEFAULT;
    }
}
