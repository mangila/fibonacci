package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.shared.FibonacciAlgorithm;
import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.ops.EnsureNumberOps;
import org.jobrunr.jobs.lambdas.JobRequest;

public record ProducerJobRequest(int limit, FibonacciAlgorithm algorithm) implements JobRequest {

    private static final EnsureNumberOps ENSURE_NUMBER_OPS = Ensure.numbers();

    public ProducerJobRequest {
        ENSURE_NUMBER_OPS.positive(limit);
        Ensure.notNull(algorithm);
    }

    @Override
    public Class<ProducerJobHandler> getJobRequestHandler() {
        return ProducerJobHandler.class;
    }

}
