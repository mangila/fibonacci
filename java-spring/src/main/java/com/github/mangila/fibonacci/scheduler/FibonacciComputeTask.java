package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.config.FibonacciComputeTaskConfig;
import com.github.mangila.fibonacci.model.FibonacciResult;
import io.github.mangila.ensure4j.Ensure;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public record FibonacciComputeTask(FibonacciComputeTaskConfig config) implements Callable<List<FibonacciResult>> {

    /**
     * Computes subsequent Fibonacci numbers given prior state
     */
    @Override
    public List<FibonacciResult> call() {
        Ensure.notNull(config);
        final int limit = config.limit();
        int offset = config.latestPair().current().sequence();
        final List<FibonacciResult> fibonacciResults = new ArrayList<>(limit);
        BigDecimal previous = config.latestPair().previous().result();
        BigDecimal current = config.latestPair().current().result();
        for (int i = 0; i < limit; i++) {
            BigDecimal next = previous.add(current);
            previous = current;
            current = next;
            fibonacciResults.add(new FibonacciResult(offset, next, next.precision()));
            offset++;
        }
        return fibonacciResults;
    }
}