package com.github.mangila.fibonacci.jobrunr.job.zset.drain;

import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.ops.EnsureNumberOps;
import org.jobrunr.jobs.lambdas.JobRequest;

public record DrainZsetJobRequest(int limit) implements JobRequest {

    private static final EnsureNumberOps ENSURE_NUMBER_OPS = Ensure.numbers();

    public DrainZsetJobRequest {
        ENSURE_NUMBER_OPS.positive(limit);
    }

    @Override
    public Class<DrainZsetJobHandler> getJobRequestHandler() {
        return DrainZsetJobHandler.class;
    }


}
