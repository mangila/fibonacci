package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.FibonacciAlgorithm;
import com.github.mangila.fibonacci.config.FibonacciProperties;
import com.github.mangila.fibonacci.db.FibonacciRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FibonacciScheduler {

    private static final Logger log = LoggerFactory.getLogger(FibonacciScheduler.class);

    private final SimpleAsyncTaskScheduler simpleAsyncTaskScheduler;
    private final ThreadPoolTaskExecutor computeAsyncTaskExecutor;
    private final SimpleAsyncTaskExecutor ioAsyncTaskExecutor;
    private final FibonacciRepository repository;
    private final FibonacciProperties fibonacciProperties;
    private final AtomicInteger fibonacciSequence;

    public FibonacciScheduler(SimpleAsyncTaskScheduler simpleAsyncTaskScheduler,
                              ThreadPoolTaskExecutor computeAsyncTaskExecutor,
                              SimpleAsyncTaskExecutor ioAsyncTaskExecutor,
                              FibonacciRepository repository, FibonacciProperties fibonacciProperties) {
        this.simpleAsyncTaskScheduler = simpleAsyncTaskScheduler;
        this.computeAsyncTaskExecutor = computeAsyncTaskExecutor;
        this.ioAsyncTaskExecutor = ioAsyncTaskExecutor;
        this.repository = repository;
        this.fibonacciProperties = fibonacciProperties;
        this.fibonacciSequence = new AtomicInteger(fibonacciProperties.getOffset());
    }

    @PostConstruct
    void init() {
        simpleAsyncTaskScheduler.scheduleWithFixedDelay(this::insertComputeTask, fibonacciProperties.getDelay());
    }

    public void insertComputeTask() {
        final int offset = fibonacciSequence.get();
        final FibonacciAlgorithm algorithm = fibonacciProperties.getAlgorithm();
        final int limit = fibonacciProperties.getLimit();
        if (offset >= limit) {
            log.info("Fibonacci computation limit reached: {}", limit);
            return;
        }
        log.info("Fibonacci computation for sequence {} with algorithm {}", offset, algorithm);
        var task = new FibonacciComputeTask(algorithm, offset);
        // Spawn a platform thread for CPU stuffs
        var fibCompute = CompletableFuture.supplyAsync(task::call, computeAsyncTaskExecutor)
                // Then spawn a virtual thread for IO stuffs
                .thenAcceptAsync(repository::insert, ioAsyncTaskExecutor)
                .orTimeout(3, TimeUnit.MINUTES)
                .exceptionally(e -> {
                    var message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    log.error("Fibonacci computation failed for sequence {}: {}", offset, message);
                    return null;
                });
        fibCompute.join();
        fibonacciSequence.incrementAndGet();
    }

}
