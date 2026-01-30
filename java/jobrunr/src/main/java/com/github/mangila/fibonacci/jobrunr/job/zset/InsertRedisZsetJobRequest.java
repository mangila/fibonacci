package com.github.mangila.fibonacci.jobrunr.job.zset;

import io.github.mangila.ensure4j.Ensure;
import org.jobrunr.jobs.lambdas.JobRequest;

public record InsertRedisZsetJobRequest(int limit) implements JobRequest {

    public InsertRedisZsetJobRequest {
        Ensure.positive(limit);
    }

    @Override
    public Class<InsertRedisZsetJobHandler> getJobRequestHandler() {
        return InsertRedisZsetJobHandler.class;
    }
}
