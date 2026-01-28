package com.github.mangila.fibonacci.scheduler.jobrunr;

import org.jobrunr.jobs.lambdas.JobRequest;

public record InsertRedisStreamJobRequest(int limit) implements JobRequest {

    @Override
    public Class<InsertRedisStreamJobHandler> getJobRequestHandler() {
        return InsertRedisStreamJobHandler.class;
    }
}
