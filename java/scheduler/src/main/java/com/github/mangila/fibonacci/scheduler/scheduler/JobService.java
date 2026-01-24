package com.github.mangila.fibonacci.scheduler.scheduler;

import com.github.mangila.fibonacci.scheduler.properties.FibonacciProperties;
import com.github.mangila.fibonacci.scheduler.repository.FibonacciRepository;
import com.github.mangila.fibonacci.scheduler.task.FibonacciComputeTask;
import org.jobrunr.jobs.annotations.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    private final FibonacciProperties properties;
    private final ThreadPoolTaskExecutor computeAsyncTaskExecutor;
    private final FibonacciRepository repository;
    private final ReentrantLock delayLock = new ReentrantLock(true);

    public JobService(FibonacciProperties properties,
                      ThreadPoolTaskExecutor computeAsyncTaskExecutor,
                      FibonacciRepository repository) {
        this.properties = properties;
        this.computeAsyncTaskExecutor = computeAsyncTaskExecutor;
        this.repository = repository;
    }

    @Job(name = "Fibonacci job for number %0", retries = 3)
    public void computeFibonacci(int sequence) {
        var task = new FibonacciComputeTask(properties.getAlgorithm(), sequence);
        var future = CompletableFuture.supplyAsync(task::call, computeAsyncTaskExecutor)
                .orTimeout(3, TimeUnit.MINUTES);
        var fibonacciResult = future.join();
        delayLock.lock();
        try {
            TimeUnit.MILLISECONDS.sleep(properties.getDelay().toMillis());
            repository.save(fibonacciResult);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while sleeping for delay", e);
        } finally {
            delayLock.unlock();
        }
    }

}
