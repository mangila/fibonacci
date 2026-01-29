package com.github.mangila.fibonacci.jobrunr.task;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.core.FibonacciCalculator;
import com.github.mangila.fibonacci.jobrunr.model.FibonacciComputeResult;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.Callable;

/**
 * No memoization is implemented, let it compute every number
 * @param algorithm
 * @param sequence
 */
public record FibonacciComputeTask(FibonacciAlgorithm algorithm, int sequence) implements Callable<FibonacciComputeResult> {

    @Override
    public FibonacciComputeResult call() {
        BigInteger fib = switch (algorithm) {
            case FAST_DOUBLING -> FibonacciCalculator.fastDoubling(sequence);
            case ITERATIVE -> FibonacciCalculator.iterative(sequence);
            case RECURSIVE -> FibonacciCalculator.naiveRecursive(sequence);
        };
        return FibonacciComputeResult.of(sequence, new BigDecimal(fib));
    }
}
