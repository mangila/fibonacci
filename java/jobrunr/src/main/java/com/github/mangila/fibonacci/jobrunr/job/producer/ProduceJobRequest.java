package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import io.github.mangila.ensure4j.Ensure;
import org.jobrunr.jobs.lambdas.JobRequest;

public record ProduceJobRequest(int limit, FibonacciAlgorithm algorithm) implements JobRequest {

    public ProduceJobRequest {
        Ensure.positive(limit);
        Ensure.notNull(algorithm);
    }

    @Override
    public Class<ProduceJobHandler> getJobRequestHandler() {
        return ProduceJobHandler.class;
    }

}
