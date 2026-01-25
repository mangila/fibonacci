package com.github.mangila.fibonacci.scheduler.scheduler;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.core.model.FibonacciCommand;
import io.github.mangila.ensure4j.Ensure;
import org.jobrunr.jobs.JobId;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class Scheduler {

    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    private final TaskService taskService;
    private final JobScheduler jobScheduler;
    private final JobService jobService;

    public Scheduler(TaskService taskService,
                     JobScheduler jobScheduler,
                     JobService jobService) {
        this.taskService = taskService;
        this.jobScheduler = jobScheduler;
        this.jobService = jobService;
    }

    public UUID scheduleFibonacciCalculation(FibonacciCommand command) {
        final int offset = command.offset();
        final int limit = command.limit();
        final FibonacciAlgorithm algorithm = command.algorithm();
        Ensure.isTrue(algorithm.isSuitable(offset, limit), "Algorithm is not suitable for large sequences");
        log.info("Scheduling Fibonacci computation for {}", command);
        var future = taskService.submitSequenceFilter(offset, limit)
                .orTimeout(30, TimeUnit.SECONDS)
                .exceptionally((throwable) -> {
                    log.error("Error while scheduling Fibonacci computations", throwable);
                    return List.of();
                });
        List<Integer> sequences = future.join();
        Ensure.notEmpty(sequences, "Sequences already computed");
        JobId id = jobScheduler.enqueue(() -> jobService.enqueueFibonacciTasks(command));
        return id.asUUID();
    }
}
