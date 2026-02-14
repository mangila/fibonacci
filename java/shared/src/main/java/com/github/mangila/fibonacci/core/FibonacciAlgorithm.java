package com.github.mangila.fibonacci.core;

import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.ops.EnsureNumberOps;

public enum FibonacciAlgorithm {
    RECURSIVE,
    ITERATIVE,
    FAST_DOUBLING;

    private static final EnsureNumberOps ENSURE_NUMBER_OPS = Ensure.numbers();

    public boolean isSuitable(int sequence) {
        ENSURE_NUMBER_OPS.positive(sequence);
        return switch (this) {
            case RECURSIVE -> sequence <= 30;
            case ITERATIVE -> sequence <= 100_000;
            case FAST_DOUBLING -> true;
        };
    }
}
