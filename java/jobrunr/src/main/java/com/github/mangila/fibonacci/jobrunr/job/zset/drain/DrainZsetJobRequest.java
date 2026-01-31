package com.github.mangila.fibonacci.jobrunr.job.zset.drain;

import io.github.mangila.ensure4j.Ensure;
import org.jobrunr.jobs.lambdas.JobRequest;

public record DrainZsetJobRequest(int limit) implements JobRequest {

    public DrainZsetJobRequest {
        Ensure.positive(limit);
    }

    @Override
    public Class<DrainZsetJobHandler> getJobRequestHandler() {
        return DrainZsetJobHandler.class;
    }
}
