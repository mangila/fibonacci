package com.github.mangila.fibonacci.jobrunr.job;

import org.jobrunr.jobs.lambdas.JobRequest;

public record DrainZsetJobRequest(int limit) implements JobRequest {
    @Override
    public Class<DrainZsetJobHandler> getJobRequestHandler() {
        return DrainZsetJobHandler.class;
    }
}
