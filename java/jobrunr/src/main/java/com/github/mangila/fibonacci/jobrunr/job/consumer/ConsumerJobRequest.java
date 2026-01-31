package com.github.mangila.fibonacci.jobrunr.job.consumer;

import io.github.mangila.ensure4j.Ensure;
import org.jobrunr.jobs.lambdas.JobRequest;

public record ConsumerJobRequest(int limit) implements JobRequest {

    public ConsumerJobRequest {
        Ensure.positive(limit);
    }

    @Override
    public Class<ConsumerJobHandler> getJobRequestHandler() {
        return ConsumerJobHandler.class;
    }
}
