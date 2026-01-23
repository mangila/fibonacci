package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.scheduler.properties.FibonacciProperties;
import com.github.mangila.fibonacci.scheduler.repository.FibonacciRepository;
import com.github.mangila.fibonacci.scheduler.task.FibonacciComputeTask;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
public class SchedulerApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SchedulerApplication.class);
    private final ReentrantLock delayLock = new ReentrantLock(true);
    private final ThreadPoolTaskExecutor computeAsyncTaskExecutor;
    private final FibonacciProperties properties;
    private final FibonacciRepository repository;
    private final JobScheduler jobScheduler;

    public SchedulerApplication(ThreadPoolTaskExecutor computeAsyncTaskExecutor,
                                FibonacciProperties properties,
                                FibonacciRepository repository,
                                JobScheduler jobScheduler) {
        this.computeAsyncTaskExecutor = computeAsyncTaskExecutor;
        this.properties = properties;
        this.repository = repository;
        this.jobScheduler = jobScheduler;
    }

    static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        for (int i = properties.getOffset(); i <= properties.getLimit(); i++) {
            final int sequence = i;
            if (repository.hasSequence(sequence)) {
                log.info("Skipping sequence {} as it already exists", sequence);
                continue;
            }
            jobScheduler.enqueue(() -> compute(sequence));
        }
    }

    public void compute(int sequence) {
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
