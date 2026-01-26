package com.github.mangila.fibonacci.core;

public enum FibonacciAlgorithm {
    RECURSIVE,
    ITERATIVE,
    FAST_DOUBLING;

    public boolean isSuitable(int start, int end) {
        return switch (this) {
            case RECURSIVE -> start <= 10 && end <= 20;
            case ITERATIVE -> start <= 100_000;
            case FAST_DOUBLING -> true;
        };
    }
}
