package com.github.mangila.fibonacci.jobrunr.job.consumer.compute;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.core.FibonacciCalculator;
import com.github.mangila.fibonacci.jobrunr.job.consumer.compute.model.FibonacciComputeResult;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.Callable;

/**
 * No memoization is implemented, let it compute every number
 *
 * @param algorithm
 * @param sequence
 */
public record ComputeTask(FibonacciAlgorithm algorithm, int sequence) implements Callable<FibonacciComputeResult> {

    private static final FibonacciCalculator CALCULATOR = FibonacciCalculator.INSTANCE;

    @Override
    public FibonacciComputeResult call() {
        BigInteger fib = switch (algorithm) {
            case FAST_DOUBLING -> CALCULATOR.fastDoubling(sequence);
            case ITERATIVE -> CALCULATOR.iterative(sequence);
            case RECURSIVE -> CALCULATOR.naiveRecursive(sequence);
        };
        return FibonacciComputeResult.of(sequence, new BigDecimal(fib));
    }
}
