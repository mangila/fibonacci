package com.github.mangila.fibonacci.jobrunr.job.zset.insert;

import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.ops.EnsureNumberOps;
import org.jobrunr.jobs.lambdas.JobRequest;

public record InsertZsetJobRequest(int limit) implements JobRequest {

    private static final EnsureNumberOps ENSURE_NUMBER_OPS = Ensure.numbers();

    public InsertZsetJobRequest {
        ENSURE_NUMBER_OPS.positive(limit);
    }

    @Override
    public Class<InsertZsetJobHandler> getJobRequestHandler() {
        return InsertZsetJobHandler.class;
    }
}
