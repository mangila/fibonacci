package com.github.mangila.fibonacci.jobrunr.job.consumer;

import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.ops.EnsureNumberOps;
import org.jobrunr.jobs.lambdas.JobRequest;

public record ConsumerJobRequest(int limit) implements JobRequest {

    private static final EnsureNumberOps ENSURE_NUMBER_OPS = Ensure.numbers();

    public ConsumerJobRequest {
        ENSURE_NUMBER_OPS.positive(limit);
    }

    @Override
    public Class<ConsumerJobHandler> getJobRequestHandler() {
        return ConsumerJobHandler.class;
    }
}
