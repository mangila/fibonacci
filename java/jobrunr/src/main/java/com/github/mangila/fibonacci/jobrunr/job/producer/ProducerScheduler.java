package com.github.mangila.fibonacci.jobrunr.job.producer;

import org.jobrunr.scheduling.JobRequestScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

import static org.jobrunr.scheduling.JobBuilder.aJob;

@Service
public class ProducerScheduler {

    private static final Logger log = LoggerFactory.getLogger(ProducerScheduler.class);

    private final ProducerProperties properties;
    private final JobRequestScheduler jobRequestScheduler;

    public ProducerScheduler(ProducerProperties properties,
                             JobRequestScheduler jobRequestScheduler) {
        this.properties = properties;
        this.jobRequestScheduler = jobRequestScheduler;
    }

    @EventListener(ApplicationReadyEvent.class)
    void schedule() {
        if (properties.isEnabled()) {
            log.info("Producer scheduling is enabled");
            final var limit = properties.getLimit();
            final var algorithm = properties.getAlgorithm();
            final var batchSize = properties.getBatchSize();
            ProducerJobRequest request = new ProducerJobRequest(batchSize, limit, algorithm);
            UUID uuid = jobRequestScheduler.create(aJob()
                            .scheduleIn(Duration.ofSeconds(1))
                            .withName("Produce Fibonacci Calculations Limit: %s".formatted(limit))
                            .withAmountOfRetries(10)
                            .withLabels("produce")
                            .withJobRequest(request))
                    .asUUID();
            log.info("Scheduled producer job: {}", uuid);
        }
    }
}
