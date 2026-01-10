package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.Fibonacci;
import com.github.mangila.fibonacci.db.FibonacciRepository;
import com.github.mangila.fibonacci.model.FibonacciOption;
import com.github.mangila.fibonacci.model.FibonacciState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public record FibonacciTask(FibonacciRepository repository, FibonacciOption option) implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(FibonacciTask.class);

    @Override
    public void run() {
        final int limit = option.limit();
        final long offset = option.offset();
        final List<FibonacciCompute> batchBuffer = new ArrayList<>(limit);
        log.info("Starting FibonacciTask from offset {} - limit {}", offset, limit);
        // Generates and persists Fibonacci sequence; handles errors
        try {
            FibonacciState state = Fibonacci.generate(offset);
            long nextOffset = offset + 2;
            BigInteger previous = state.previous();
            BigInteger current = state.current();
            batchBuffer.add(new FibonacciCompute(offset, previous));
            batchBuffer.add(new FibonacciCompute(nextOffset - 1, current));
            // Iterates, computes, and buffers Fibonacci sequence
            for (long i = 2; i < limit; i++) {
                // Persists buffered results; introduces random delays
                if (i % 100 == 0) {
                    delay(durationJitterInMillis(10, 50));
                    repository.batchInsert(batchBuffer);
                    batchBuffer.clear();
                    delay(durationJitterInMillis(100, 500));
                }
                BigInteger next = Fibonacci.FIBONACCI_NEXT_CACHE.getIfPresent(i);
                if (next == null) {
                    next = previous.add(current);
                    Fibonacci.FIBONACCI_NEXT_CACHE.put(i, next);
                }
                previous = current;
                current = next;
                batchBuffer.add(new FibonacciCompute(nextOffset, next));
                nextOffset++;
            }
            delay(durationJitterInMillis(10, 50));
            repository.batchInsert(batchBuffer);
            batchBuffer.clear();
            delay(durationJitterInMillis(100, 500));
            log.info("FibonacciTask finished");
        } catch (Exception e) {
            log.error("Error in FibonacciTask", e);
            Thread.currentThread().interrupt();
        }
    }

    private void delay(Duration duration) {
        try {
            TimeUnit.MILLISECONDS.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private Duration durationJitterInMillis(int origin, int bound) {
        return Duration.ofMillis(ThreadLocalRandom.current().nextInt(origin, bound));
    }
}