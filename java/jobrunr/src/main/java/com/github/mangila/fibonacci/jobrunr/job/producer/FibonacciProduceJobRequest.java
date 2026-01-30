package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import org.jobrunr.jobs.lambdas.JobRequest;

public record FibonacciProduceJobRequest(int limit, FibonacciAlgorithm algorithm) implements JobRequest {
    @Override
    public Class<FibonacciProduceJobHandler> getJobRequestHandler() {
        return FibonacciProduceJobHandler.class;
    }
}
