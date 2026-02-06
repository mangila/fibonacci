package com.github.mangila.fibonacci.jobrunr.job.consumer.compute;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import io.github.mangila.ensure4j.Ensure;
import org.jobrunr.jobs.lambdas.JobRequest;

public record ComputeJobRequest(int sequence, FibonacciAlgorithm algorithm) implements JobRequest {

    public ComputeJobRequest {
        Ensure.positive(sequence);
        Ensure.notNull(algorithm);
        Ensure.isTrue(algorithm.isSuitable(sequence));
    }

    @Override
    public Class<ComputeJobHandler> getJobRequestHandler() {
        return ComputeJobHandler.class;
    }
}
