package com.github.mangila.fibonacci.jobrunr.job.producer;

import org.jobrunr.scheduling.JobRequestScheduler;
import org.jobrunr.scheduling.RecurringJobBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

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
        log.info("Producer is enabled");
        final var limit = properties.getLimit();
        final var algorithm = properties.getAlgorithm();
        final var cron = properties.getCron();
        var job = RecurringJobBuilder.aRecurringJob()
                .withCron(cron)
                .withName("Produce fibonacci numbers")
                .withJobRequest(new ProducerJobRequest(limit, algorithm))
                .withLabels("producer")
                .withAmountOfRetries(3);
        jobRequestScheduler.createRecurrently(job);
    }
}
