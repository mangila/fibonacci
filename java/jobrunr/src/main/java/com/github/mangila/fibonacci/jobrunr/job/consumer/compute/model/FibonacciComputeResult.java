package com.github.mangila.fibonacci.jobrunr.job.consumer.compute.model;

import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.ops.EnsureNumberOps;
import org.jobrunr.jobs.context.JobContext;

import java.math.BigDecimal;

public record FibonacciComputeResult(int sequence, BigDecimal result, int precision) implements JobContext.StepResult {

    private static final EnsureNumberOps ENSURE_NUMBER_OPS = Ensure.numbers();

    public FibonacciComputeResult {
        ENSURE_NUMBER_OPS.positive(sequence);
        Ensure.notNull(result);
        ENSURE_NUMBER_OPS.positive(precision);
    }

    public static FibonacciComputeResult of(int sequence, BigDecimal bigDecimal) {
        return new FibonacciComputeResult(sequence, bigDecimal, bigDecimal.precision());
    }
}
