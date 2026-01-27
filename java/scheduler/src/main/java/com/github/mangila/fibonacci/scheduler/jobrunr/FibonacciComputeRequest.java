package com.github.mangila.fibonacci.scheduler.jobrunr;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import org.jobrunr.jobs.lambdas.JobRequest;

public record FibonacciComputeRequest(int sequence, FibonacciAlgorithm algorithm) implements JobRequest {

    @Override
    public Class<FibonacciComputeHandler> getJobRequestHandler() {
        return FibonacciComputeHandler.class;
    }
}
