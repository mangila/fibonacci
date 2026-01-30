package com.github.mangila.fibonacci.jobrunr.job.model;

import io.github.mangila.ensure4j.Ensure;
import org.jobrunr.jobs.context.JobContext;

import java.math.BigDecimal;

public record FibonacciComputeResult(int sequence, BigDecimal result, int precision) implements JobContext.StepResult {

    public FibonacciComputeResult {
        Ensure.positive(sequence);
        Ensure.notNull(result);
        Ensure.positive(precision);
    }

    public static FibonacciComputeResult of(int sequence, BigDecimal bigDecimal) {
        return new FibonacciComputeResult(sequence, bigDecimal, bigDecimal.precision());
    }
}
