package com.github.mangila.fibonacci.jobrunr.job.consumer.compute;

import org.jobrunr.scheduling.JobRequestScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.jobrunr.scheduling.JobBuilder.aJob;

@Service
public class ComputeScheduler {

    private final JobRequestScheduler jobRequestScheduler;

    public ComputeScheduler(JobRequestScheduler jobRequestScheduler) {
        this.jobRequestScheduler = jobRequestScheduler;
    }

    public UUID schedule(ComputeJobRequest request) {
        final var sequence = request.sequence();
        final var jitter = ThreadLocalRandom.current().nextInt(5, 30);
        return jobRequestScheduler.create(aJob()
                        .scheduleIn(Duration.ofSeconds(jitter))
                        .withName("Fibonacci Calculation for sequence: %s".formatted(sequence))
                        .withAmountOfRetries(3)
                        .withLabels("compute")
                        .withJobRequest(request))
                .asUUID();
    }

}
