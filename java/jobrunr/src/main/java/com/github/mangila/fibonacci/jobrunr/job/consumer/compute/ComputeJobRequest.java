package com.github.mangila.fibonacci.jobrunr.job.consumer.compute;

import com.github.mangila.fibonacci.shared.FibonacciAlgorithm;
import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.ops.EnsureNumberOps;
import org.jobrunr.jobs.lambdas.JobRequest;

public record ComputeJobRequest(int sequence, FibonacciAlgorithm algorithm) implements JobRequest {

    private static final EnsureNumberOps ENSURE_NUMBER_OPS = Ensure.numbers();

    public ComputeJobRequest {
        ENSURE_NUMBER_OPS.positive(sequence);
        Ensure.notNull(algorithm);
        Ensure.isTrue(algorithm.isSuitable(sequence));
    }

    @Override
    public Class<ComputeJobHandler> getJobRequestHandler() {
        return ComputeJobHandler.class;
    }
}
