package com.github.mangila.fibonacci.jobrunr.job.consumer;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import io.github.mangila.ensure4j.Ensure;
import org.jobrunr.jobs.lambdas.JobRequest;

public record FibonacciComputeJobRequest(int sequence, FibonacciAlgorithm algorithm) implements JobRequest {

    public FibonacciComputeJobRequest {
        Ensure.positive(sequence);
        Ensure.notNull(algorithm);
        Ensure.isTrue(algorithm.isSuitable(sequence));
    }

    @Override
    public Class<FibonacciComputeHandler> getJobRequestHandler() {
        return FibonacciComputeHandler.class;
    }
}
