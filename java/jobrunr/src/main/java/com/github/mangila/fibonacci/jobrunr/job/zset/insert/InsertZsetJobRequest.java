package com.github.mangila.fibonacci.jobrunr.job.zset.insert;

import io.github.mangila.ensure4j.Ensure;
import org.jobrunr.jobs.lambdas.JobRequest;

public record InsertZsetJobRequest(int limit) implements JobRequest {

    public InsertZsetJobRequest {
        Ensure.positive(limit);
    }

    @Override
    public Class<InsertZsetJobHandler> getJobRequestHandler() {
        return InsertZsetJobHandler.class;
    }
}
