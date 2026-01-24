package com.github.mangila.fibonacci.scheduler.scheduler;

import com.github.mangila.fibonacci.scheduler.task.FibonacciEnqueueTask;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class Scheduler {

    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    private final SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;
    private final FibonacciEnqueueTask enqueueTask;
    private final JobScheduler jobScheduler;
    private final JobService jobService;

    public Scheduler(SimpleAsyncTaskExecutor simpleAsyncTaskExecutor,
                     FibonacciEnqueueTask enqueueTask,
                     JobScheduler jobScheduler,
                     JobService jobService) {
        this.simpleAsyncTaskExecutor = simpleAsyncTaskExecutor;
        this.enqueueTask = enqueueTask;
        this.jobScheduler = jobScheduler;
        this.jobService = jobService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("Scheduling Fibonacci sequence enqueue task");
        var _ = CompletableFuture.supplyAsync(enqueueTask::call, simpleAsyncTaskExecutor)
                .orTimeout(3, TimeUnit.MINUTES)
                .thenAcceptAsync(sequences -> {
                    log.info("Scheduling {} Fibonacci computations", sequences.size());
                    sequences.forEach(sequence -> jobScheduler.enqueue(() -> jobService.computeFibonacci(sequence)));
                }, simpleAsyncTaskExecutor)
                .exceptionallyAsync(ex -> {
                    log.error("Failed to enqueue Fibonacci sequences", ex);
                    return null;
                }, simpleAsyncTaskExecutor);
    }
}
