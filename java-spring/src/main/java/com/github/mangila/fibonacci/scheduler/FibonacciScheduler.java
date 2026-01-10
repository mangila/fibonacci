package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.db.FibonacciRepository;
import com.github.mangila.fibonacci.model.FibonacciOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class FibonacciScheduler {

    private static final Logger log = LoggerFactory.getLogger(FibonacciScheduler.class);

    private final ThreadPoolTaskExecutor computeAsyncTaskExecutor;
    private final FibonacciRepository repository;

    public FibonacciScheduler(ThreadPoolTaskExecutor computeAsyncTaskExecutor,
                              FibonacciRepository repository) {
        this.computeAsyncTaskExecutor = computeAsyncTaskExecutor;
        this.repository = repository;
    }

    @Scheduled(
            fixedDelay = 5,
            timeUnit = TimeUnit.SECONDS,
            scheduler = "simpleAsyncTaskScheduler")
    public void run() {
        log.info("Scheduled Fibonacci task started");
        long nextOffset = repository.nextOffset();
        var option = new FibonacciOption(nextOffset, 1000);
        var task = new FibonacciTask(repository, option);
        // Run on platform thread for CPU stuffs
        var future = computeAsyncTaskExecutor.submitCompletable(task);
        future.join();
        log.info("Scheduled Fibonacci task finished");
    }

}
