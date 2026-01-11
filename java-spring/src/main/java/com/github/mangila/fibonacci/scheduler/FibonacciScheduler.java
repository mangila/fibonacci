package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.db.FibonacciRepository;
import com.github.mangila.fibonacci.model.FibonacciOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class FibonacciScheduler {

    private static final Logger log = LoggerFactory.getLogger(FibonacciScheduler.class);

    private final ThreadPoolTaskExecutor computeAsyncTaskExecutor;
    private final SimpleAsyncTaskExecutor ioAsyncTaskExecutor;
    private final FibonacciRepository repository;

    public FibonacciScheduler(ThreadPoolTaskExecutor computeAsyncTaskExecutor,
                              SimpleAsyncTaskExecutor ioAsyncTaskExecutor,
                              FibonacciRepository repository) {
        this.computeAsyncTaskExecutor = computeAsyncTaskExecutor;
        this.ioAsyncTaskExecutor = ioAsyncTaskExecutor;
        this.repository = repository;
    }

    @Scheduled(
            fixedDelay = 1,
            timeUnit = TimeUnit.SECONDS,
            scheduler = "simpleAsyncTaskScheduler")
    public void insertBatch() {
        log.info("Scheduled Fibonacci task started");
        int nextOffset = repository.nextOffset();
        var option = new FibonacciOption(nextOffset, 100);
        var task = new FibonacciTask(option);
        // Run on platform thread for CPU stuffs
        var insertBatchFuture = CompletableFuture.supplyAsync(task::call, computeAsyncTaskExecutor)
                .thenAcceptAsync(repository::batchInsert, ioAsyncTaskExecutor);
        insertBatchFuture.join();
        log.info("Scheduled Fibonacci task finished");
    }

}
