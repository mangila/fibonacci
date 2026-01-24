package com.github.mangila.fibonacci.core;

public enum FibonacciAlgorithm {
    RECURSIVE,
    ITERATIVE,
    FAST_DOUBLING;

    public boolean isSuitable(int offset, int limit) {
        return switch (this) {
            case RECURSIVE -> offset <= 10 && limit <= 20;
            case ITERATIVE -> offset <= 100_000;
            case FAST_DOUBLING -> true;
        };
    }
}
