package com.github.mangila.fibonacci.jobrunr.job.consumer;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import org.jobrunr.jobs.lambdas.JobRequest;

public record FibonacciComputeJobRequest(int sequence, FibonacciAlgorithm algorithm) implements JobRequest {

    @Override
    public Class<FibonacciComputeHandler> getJobRequestHandler() {
        return FibonacciComputeHandler.class;
    }
}
