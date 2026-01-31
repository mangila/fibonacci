package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import io.github.mangila.ensure4j.Ensure;
import org.jobrunr.jobs.lambdas.JobRequest;

public record ProducerJobRequest(int limit, FibonacciAlgorithm algorithm) implements JobRequest {

    public ProducerJobRequest {
        Ensure.positive(limit);
        Ensure.notNull(algorithm);
    }

    @Override
    public Class<ProducerJobHandler> getJobRequestHandler() {
        return ProducerJobHandler.class;
    }

}
