package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.FibonacciAlgorithm;
import com.github.mangila.fibonacci.config.FibonacciProperties;
import com.github.mangila.fibonacci.db.FibonacciRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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
    private final AtomicInteger fibonacciLimit;

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
        this.fibonacciLimit = new AtomicInteger(fibonacciProperties.getLimit());
    }

    @EventListener(ApplicationReadyEvent.class)
    void init() {
        simpleAsyncTaskScheduler.scheduleWithFixedDelay(this::insertComputeTask, fibonacciProperties.getDelay());
    }

    public void insertComputeTask() {
        final int offset = fibonacciSequence.getAndIncrement();
        if (repository.hasSequence(offset)) {
            log.info("Fibonacci sequence {} already computed", offset);
            return;
        }
        final FibonacciAlgorithm algorithm = fibonacciProperties.getAlgorithm();
        if (fibonacciLimit.get() == 0) {
            log.info("Fibonacci computation limit reached, closing scheduler");
            simpleAsyncTaskScheduler.close();
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
        fibonacciLimit.decrementAndGet();
    }

}
