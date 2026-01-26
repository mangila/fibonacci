package com.github.mangila.fibonacci.core;

import io.github.mangila.ensure4j.Ensure;

public enum FibonacciAlgorithm {
    RECURSIVE,
    ITERATIVE,
    FAST_DOUBLING;

    public boolean isSuitable(int offset) {
        Ensure.positive(offset);
        return switch (this) {
            case RECURSIVE -> offset <= 30;
            case ITERATIVE -> offset <= 100_000;
            case FAST_DOUBLING -> true;
        };
    }
}
