package com.github.mangila.fibonacci.jobrunr.job.zset.model;

import com.github.mangila.fibonacci.postgres.FibonacciMetadataProjection;
import org.jobrunr.jobs.context.JobContext;

import java.util.List;

public record StepSuccess(List<FibonacciMetadataProjection> metadata) implements JobContext.StepResult {

}
