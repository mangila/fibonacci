package com.github.mangila.fibonacci.model;

public record FibonacciOption(
        int delayInMillis,
        int index,
        int iterations
) {
}