package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.config.FibonacciComputeTaskConfig;
import com.github.mangila.fibonacci.db.FibonacciRepository;
import com.github.mangila.fibonacci.model.FibonacciPair;
import com.github.mangila.fibonacci.model.FibonacciResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public void insertComputeBatch() {
        log.info("Scheduled Fibonacci task started");
        FibonacciPair latestPair = repository.queryLatestPairOrDefault();
        // Persists the first Fibonacci sequence if the table is empty
        if (latestPair.isDefault()) {
            var l = new ArrayList<FibonacciResult>();
            l.add(latestPair.previous());
            l.add(latestPair.current());
            repository.batchInsert(l);
        }
        var task = new FibonacciComputeTask(new FibonacciComputeTaskConfig(
                latestPair,
                100
        ));
        // Spawn a platform thread for CPU stuffs
        var insertBatchFuture = CompletableFuture.supplyAsync(task::call, computeAsyncTaskExecutor)
                // Then spawn a virtual thread for IO stuffs
                .thenAcceptAsync(repository::batchInsert, ioAsyncTaskExecutor);
        // Then block it back to the scheduler thread
        insertBatchFuture.join();
        log.info("Scheduled Fibonacci task finished");
    }

}
