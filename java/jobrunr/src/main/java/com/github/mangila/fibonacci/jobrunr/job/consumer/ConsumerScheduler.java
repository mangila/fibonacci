package com.github.mangila.fibonacci.jobrunr.job.consumer;

import org.jobrunr.scheduling.JobRequestScheduler;
import org.jobrunr.scheduling.RecurringJobBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerScheduler {

    private static final Logger log = LoggerFactory.getLogger(ConsumerScheduler.class);

    private final ConsumerProperties properties;
    private final JobRequestScheduler jobRequestScheduler;

    public ConsumerScheduler(ConsumerProperties properties, JobRequestScheduler jobRequestScheduler) {
        this.properties = properties;
        this.jobRequestScheduler = jobRequestScheduler;
    }

    @EventListener(ApplicationReadyEvent.class)
    void schedule() {
        if (properties.isEnabled()) {
            log.info("Consumer scheduling is enabled");
            final var limit = properties.getLimit();
            final var cron = properties.getCron();
            var job = RecurringJobBuilder.aRecurringJob()
                    .withCron(cron)
                    .withName("Consume fibonacci numbers")
                    .withJobRequest(new ConsumerJobRequest(limit))
                    .withLabels("consumer")
                    .withAmountOfRetries(3);
            jobRequestScheduler.createRecurrently(job);
        }
    }
}
