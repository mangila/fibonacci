package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.FibonacciAlgorithm;
import com.github.mangila.fibonacci.FibonacciCalculator;
import com.github.mangila.fibonacci.shared.FibonacciResult;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.Callable;

/**
 * No memoization is implemented here. Let it compute the Fibonacci number.
 *
 * @param algorithm - the algorithm to use for computing Fibonacci number
 * @param sequence     - the sequence of the Fibonacci number to compute
 */
public record FibonacciComputeTask(FibonacciAlgorithm algorithm, int sequence) implements Callable<FibonacciResult> {

    @Override
    public FibonacciResult call() {
        BigInteger fib = switch (algorithm) {
            case FAST_DOUBLING -> FibonacciCalculator.fastDoubling(sequence);
            case ITERATIVE -> FibonacciCalculator.iterative(sequence);
            case RECURSIVE -> FibonacciCalculator.naiveRecursive(sequence);
        };
        return FibonacciResult.of(sequence,new BigDecimal(fib));
    }
}