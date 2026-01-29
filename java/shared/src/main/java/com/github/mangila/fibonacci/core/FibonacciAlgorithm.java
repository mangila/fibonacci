package com.github.mangila.fibonacci.core;

import io.github.mangila.ensure4j.Ensure;

public enum FibonacciAlgorithm {
    RECURSIVE,
    ITERATIVE,
    FAST_DOUBLING;

    public boolean isSuitable(int sequence) {
        Ensure.positive(sequence);
        return switch (this) {
            case RECURSIVE -> sequence <= 30;
            case ITERATIVE -> sequence <= 100_000;
            case FAST_DOUBLING -> true;
        };
    }
}
