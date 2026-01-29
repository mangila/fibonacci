package com.github.mangila.fibonacci.core;

import io.github.mangila.ensure4j.Ensure;

public record FibonacciComputeRequest(int sequence, FibonacciAlgorithm algorithm) {

    public FibonacciComputeRequest {
        Ensure.positive(sequence);
        Ensure.notNull(algorithm);
        Ensure.isTrue(algorithm.isSuitable(sequence));
    }
}
