package com.github.mangila.fibonacci.config;


import com.github.mangila.fibonacci.model.FibonacciPair;

public record FibonacciComputeTaskConfig(
        FibonacciPair latestPair,
        int limit
) {
}
