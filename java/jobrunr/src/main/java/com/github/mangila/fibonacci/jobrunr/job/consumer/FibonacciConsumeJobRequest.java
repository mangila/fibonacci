package com.github.mangila.fibonacci.jobrunr.job.consumer;

import org.jobrunr.jobs.lambdas.JobRequest;

public record FibonacciConsumeJobRequest(int limit) implements JobRequest {
    @Override
    public Class<FibonacciConsumeJobHandler> getJobRequestHandler() {
        return FibonacciConsumeJobHandler.class;
    }
}
