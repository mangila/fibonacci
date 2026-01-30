package com.github.mangila.fibonacci.jobrunr.job.zset;

import org.jobrunr.jobs.lambdas.JobRequest;

public record DrainZsetJobRequest(int limit) implements JobRequest {
    @Override
    public Class<DrainZsetJobHandler> getJobRequestHandler() {
        return DrainZsetJobHandler.class;
    }
}
