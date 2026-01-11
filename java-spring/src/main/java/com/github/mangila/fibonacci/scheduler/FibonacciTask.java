package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.Fibonacci;
import com.github.mangila.fibonacci.model.FibonacciOption;
import com.github.mangila.fibonacci.model.FibonacciState;
import io.github.mangila.ensure4j.Ensure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public record FibonacciTask(FibonacciOption option) implements Callable<List<FibonacciCompute>> {

    private static final Logger log = LoggerFactory.getLogger(FibonacciTask.class);

    @Override
    public List<FibonacciCompute> call() {
        Ensure.notNull(option);
        final int limit = option.limit();
        final int offset = option.offset();
        final List<FibonacciCompute> fibonacciComputes = new ArrayList<>(limit);
        log.info("Starting FibonacciTask from offset {} - limit {}", offset, limit);
        FibonacciState state = Fibonacci.generate(offset);
        int nextOffset = offset + 2;
        BigInteger previous = state.previous();
        BigInteger current = state.current();
        fibonacciComputes.add(new FibonacciCompute(offset, previous));
        fibonacciComputes.add(new FibonacciCompute(nextOffset - 1, current));
        for (int i = 2; i < limit; i++) {
            BigInteger next = previous.add(current);
            previous = current;
            current = next;
            fibonacciComputes.add(new FibonacciCompute(nextOffset, next));
            nextOffset++;
        }
        log.info("FibonacciTask finished");
        return fibonacciComputes;
    }
}