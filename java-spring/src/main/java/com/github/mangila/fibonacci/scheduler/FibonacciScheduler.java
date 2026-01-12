package com.github.mangila.fibonacci.scheduler;

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

@Service
public class FibonacciScheduler {

    private static final Logger log = LoggerFactory.getLogger(FibonacciScheduler.class);

    private final SimpleAsyncTaskScheduler simpleAsyncTaskScheduler;
    private final ThreadPoolTaskExecutor computeAsyncTaskExecutor;
    private final SimpleAsyncTaskExecutor ioAsyncTaskExecutor;
    private final FibonacciRepository repository;
    private final FibonacciProperties fibonacciProperties;

    public FibonacciScheduler(SimpleAsyncTaskScheduler simpleAsyncTaskScheduler,
                              ThreadPoolTaskExecutor computeAsyncTaskExecutor,
                              SimpleAsyncTaskExecutor ioAsyncTaskExecutor,
                              FibonacciRepository repository, FibonacciProperties fibonacciProperties) {
        this.simpleAsyncTaskScheduler = simpleAsyncTaskScheduler;
        this.computeAsyncTaskExecutor = computeAsyncTaskExecutor;
        this.ioAsyncTaskExecutor = ioAsyncTaskExecutor;
        this.repository = repository;
        this.fibonacciProperties = fibonacciProperties;
    }

    @PostConstruct
    void init() {
        simpleAsyncTaskScheduler.scheduleWithFixedDelay(this::insertComputeTask, fibonacciProperties.getDelay());
    }

    public void insertComputeTask() {
        var latestSequence = repository.latestSequence();
        var nextSequence = latestSequence + 1;
        var algorithm = fibonacciProperties.getAlgorithm();
        var limit = fibonacciProperties.getLimit();
        if (nextSequence >= limit) {
            log.info("Fibonacci computation limit reached: {}", limit);
            return;
        }
        log.info("Fibonacci computation for sequence {} with algorithm {}", nextSequence, algorithm);
        var task = new FibonacciComputeTask(algorithm, nextSequence);
        // Spawn a platform thread for CPU stuffs
        var fibCompute = CompletableFuture.supplyAsync(task::call, computeAsyncTaskExecutor)
                // Then spawn a virtual thread for IO stuffs
                .thenAcceptAsync(repository::insert, ioAsyncTaskExecutor)
                .orTimeout(3, TimeUnit.MINUTES)
                .exceptionally(e -> {
                    var message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    log.error("Fibonacci computation failed for sequence {}: {}", nextSequence, message);
                    return null;
                });
        fibCompute.join();
    }

}
