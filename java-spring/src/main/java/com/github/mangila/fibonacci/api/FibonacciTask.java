package com.github.mangila.fibonacci.api;

import com.github.mangila.fibonacci.model.FibonacciOption;
import com.github.mangila.fibonacci.model.FibonacciState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

public record FibonacciTask(FibonacciOption option) implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(FibonacciTask.class);

    @Override
    public void run() {
        int offset = option.offset();
        int limit = option.limit();
        FibonacciState state = FibonacciGenerator.generate(offset);
        offset += 2;
        BigInteger previous = state.previous();
        BigInteger current = state.current();
        log.info("Fibonacci sequence {}: {}", offset - 2, previous);
        log.info("Fibonacci sequence {}: {}", offset - 1, current);
        for (int i = 2; i < limit; i++) {
            var next = previous.add(current);
            previous = current;
            current = next;
            log.info("Fibonacci sequence {}: {}", offset, current);
            offset++;
        }
    }
}
