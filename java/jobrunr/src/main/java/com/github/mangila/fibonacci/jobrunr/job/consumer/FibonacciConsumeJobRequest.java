package com.github.mangila.fibonacci.jobrunr.job.consumer;

import io.github.mangila.ensure4j.Ensure;
import org.jobrunr.jobs.lambdas.JobRequest;

public record FibonacciConsumeJobRequest(int limit) implements JobRequest {

    public FibonacciConsumeJobRequest {
        Ensure.positive(limit);
    }

    @Override
    public Class<FibonacciConsumeJobHandler> getJobRequestHandler() {
        return FibonacciConsumeJobHandler.class;
    }
}
