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

    @Override
    public FibonacciComputeResult call() {
        var instance = FibonacciCalculator.getInstance();
        BigInteger fib = switch (algorithm) {
            case FAST_DOUBLING -> instance.fastDoubling(sequence);
            case ITERATIVE -> instance.iterative(sequence);
            case RECURSIVE -> instance.naiveRecursive(sequence);
        };
        return FibonacciComputeResult.of(sequence, new BigDecimal(fib));
    }
}
