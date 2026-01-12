package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.FibonacciAlgorithm;
import com.github.mangila.fibonacci.FibonacciCalculator;
import com.github.mangila.fibonacci.model.FibonacciResult;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.Callable;

public record FibonacciComputeTask(FibonacciAlgorithm algorithm, int index) implements Callable<FibonacciResult> {

    @Override
    public FibonacciResult call() {
        BigInteger fib = switch (algorithm) {
            case FAST_DOUBLING -> FibonacciCalculator.fastDoubling(index);
            case ITERATIVE -> FibonacciCalculator.iterative(index);
            case RECURSIVE -> FibonacciCalculator.naiveRecursive(index);
            default -> throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        };
        var asDecimal = new BigDecimal(fib);
        fib = null;
        return new FibonacciResult(index, asDecimal, asDecimal.precision());
    }
}