package com.github.mangila.fibonacci.jobrunr.job.model;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.ops.EnsureNumberOps;

public record FibonacciComputeRequest(int sequence, FibonacciAlgorithm algorithm) {

    private static final EnsureNumberOps ENSURE_NUMBER_OPS = Ensure.numbers();

    public FibonacciComputeRequest {
        ENSURE_NUMBER_OPS.positive(sequence);
        Ensure.notNull(algorithm);
        Ensure.isTrue(algorithm.isSuitable(sequence));
    }
}
