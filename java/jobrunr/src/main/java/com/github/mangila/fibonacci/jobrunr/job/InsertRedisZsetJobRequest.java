package com.github.mangila.fibonacci.jobrunr.job;

import org.jobrunr.jobs.lambdas.JobRequest;

public record InsertRedisZsetJobRequest(int limit) implements JobRequest {

    @Override
    public Class<InsertRedisZsetJobHandler> getJobRequestHandler() {
        return InsertRedisZsetJobHandler.class;
    }
}
