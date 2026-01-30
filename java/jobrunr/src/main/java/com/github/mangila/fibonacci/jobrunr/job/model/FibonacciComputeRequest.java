package com.github.mangila.fibonacci.jobrunr.job.model;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import io.github.mangila.ensure4j.Ensure;

public record FibonacciComputeRequest(int sequence, FibonacciAlgorithm algorithm) {

    public FibonacciComputeRequest {
        Ensure.positive(sequence);
        Ensure.notNull(algorithm);
        Ensure.isTrue(algorithm.isSuitable(sequence));
    }
}
