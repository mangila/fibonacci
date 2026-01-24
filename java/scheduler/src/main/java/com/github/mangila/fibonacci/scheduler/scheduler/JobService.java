package com.github.mangila.fibonacci.scheduler.scheduler;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.core.model.FibonacciCommand;
import com.github.mangila.fibonacci.scheduler.repository.FibonacciRepository;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    private final TaskService taskService;
    private final JobScheduler jobScheduler;
    private final FibonacciRepository repository;
    private final ReentrantLock delayLock = new ReentrantLock(true);

    public JobService(TaskService taskService,
                      JobScheduler jobScheduler,
                      FibonacciRepository repository) {
        this.taskService = taskService;
        this.jobScheduler = jobScheduler;
        this.repository = repository;
    }

    @Job(name = "Enqueue Fibonacci jobs", retries = 3)
    public void enqueue(FibonacciCommand command) {
        final var algorithm = command.algorithm();
        final var delay = Duration.ofMillis(command.delayInMillis());
        final var offset = command.offset();
        final var limit = command.limit();
        taskService.submitSequenceFilter(offset, limit)
                .thenAccept(sequences -> {
                    log.info("Found {} sequences to schedule", sequences.size());
                    sequences.forEach(sequence -> {
                        jobScheduler.enqueue(() -> computeFibonacci(algorithm, sequence, delay));
                    });
                })
                .exceptionally((throwable) -> {
                    log.error("Error while scheduling Fibonacci computations", throwable);
                    return null;
                });
    }

    @Job(name = "Fibonacci job for number %0", retries = 3)
    public void computeFibonacci(FibonacciAlgorithm algorithm, int sequence, Duration delay) {
        var future = taskService.submitComputeTask(algorithm, sequence)
                .orTimeout(3, TimeUnit.MINUTES);
        var fibonacciResult = future.join();
        delayLock.lock();
        try {
            TimeUnit.MILLISECONDS.sleep(delay.toMillis());
            repository.save(fibonacciResult);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while sleeping for delay", e);
        } finally {
            delayLock.unlock();
        }
    }

}
