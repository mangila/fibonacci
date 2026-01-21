package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.FibonacciAlgorithm;
import com.github.mangila.fibonacci.config.FibonacciProperties;
import com.github.mangila.fibonacci.service.FibonacciService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class FibonacciTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(FibonacciTask.class);

    private final ThreadPoolTaskExecutor computeAsyncTaskExecutor;
    private final SimpleAsyncTaskExecutor ioAsyncTaskExecutor;
    private final FibonacciService service;
    private final FibonacciProperties fibonacciProperties;
    private final AtomicInteger fibonacciSequence;
    private final AtomicInteger fibonacciLimit;

    private volatile boolean limitReached = false;

    public FibonacciTask(@Qualifier("computeAsyncTaskExecutor") ThreadPoolTaskExecutor computeAsyncTaskExecutor,
                         @Qualifier("ioAsyncTaskExecutor") SimpleAsyncTaskExecutor ioAsyncTaskExecutor,
                         FibonacciService service,
                         FibonacciProperties fibonacciProperties) {
        this.computeAsyncTaskExecutor = computeAsyncTaskExecutor;
        this.ioAsyncTaskExecutor = ioAsyncTaskExecutor;
        this.service = service;
        this.fibonacciProperties = fibonacciProperties;
        this.fibonacciSequence = new AtomicInteger(fibonacciProperties.getOffset());
        this.fibonacciLimit = new AtomicInteger(fibonacciProperties.getLimit());
    }

    @Override
    public void run() {
        final int sequence = fibonacciSequence.getAndIncrement();
        if (service.hasSequence(sequence)) {
            log.info("Fibonacci sequence {} already computed", sequence);
            return;
        }
        final FibonacciAlgorithm algorithm = fibonacciProperties.getAlgorithm();
        if (fibonacciLimit.getAndDecrement() == 0) {
            limitReached = true;
            return;
        }
        log.info("Fibonacci computation for sequence {} with algorithm {}", sequence, algorithm);
        var task = new FibonacciComputeTask(algorithm, sequence);
        // Spawn a platform thread for CPU stuffs
        var fibCompute = CompletableFuture.supplyAsync(task::call, computeAsyncTaskExecutor)
                // Then spawn a virtual thread for IO stuffs
                .thenAcceptAsync(service::insert, ioAsyncTaskExecutor)
                .orTimeout(3, TimeUnit.MINUTES)
                .exceptionally(e -> {
                    var message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    log.error("Fibonacci computation failed for sequence {}: {}", sequence, message);
                    return null;
                });
        fibCompute.join();
    }

    public boolean isLimitReached() {
        return limitReached;
    }
}
