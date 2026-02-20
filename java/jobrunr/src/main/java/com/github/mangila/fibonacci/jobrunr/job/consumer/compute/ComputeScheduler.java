package com.github.mangila.fibonacci.jobrunr.job.consumer.compute;

import org.jobrunr.scheduling.JobRequestScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.jobrunr.scheduling.JobBuilder.aJob;

public class ComputeScheduler {

    private static final Logger log = LoggerFactory.getLogger(ComputeScheduler.class);

    private final JobRequestScheduler jobRequestScheduler;

    public ComputeScheduler(JobRequestScheduler jobRequestScheduler) {
        this.jobRequestScheduler = jobRequestScheduler;
    }

    public UUID schedule(ComputeJobRequest request) {
        final var sequence = request.sequence();
        final var jitter = ThreadLocalRandom.current().nextInt(5, 60);
        return jobRequestScheduler.create(aJob()
                        .scheduleIn(Duration.ofSeconds(jitter))
                        .withName("Fibonacci Calculation for sequence: (%s)".formatted(sequence))
                        .withAmountOfRetries(3)
                        .withLabels("compute")
                        .withJobRequest(request))
                .asUUID();
    }

}
