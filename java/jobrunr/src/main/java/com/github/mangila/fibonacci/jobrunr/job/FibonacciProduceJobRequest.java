package com.github.mangila.fibonacci.jobrunr.job;

import org.jobrunr.jobs.lambdas.JobRequest;

public record FibonacciProduceJobRequest(int limit) implements JobRequest {
    @Override
    public Class<FibonacciProduceJobHandler> getJobRequestHandler() {
        return FibonacciProduceJobHandler.class;
    }
}
