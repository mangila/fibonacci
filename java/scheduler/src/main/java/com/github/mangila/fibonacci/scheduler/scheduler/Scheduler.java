package com.github.mangila.fibonacci.scheduler.scheduler;

import com.github.mangila.fibonacci.core.model.FibonacciCommand;
import org.jobrunr.jobs.JobId;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class Scheduler {

    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    private final JobScheduler jobScheduler;
    private final JobService jobService;

    public Scheduler(JobScheduler jobScheduler, JobService jobService) {
        this.jobScheduler = jobScheduler;
        this.jobService = jobService;
    }

    public UUID scheduleFibonacciCalculation(FibonacciCommand command) {
        log.info("Scheduling Fibonacci computation for {}", command);
        JobId id = jobScheduler.enqueue(() -> jobService.enqueue(command));
        return id.asUUID();
    }
}
