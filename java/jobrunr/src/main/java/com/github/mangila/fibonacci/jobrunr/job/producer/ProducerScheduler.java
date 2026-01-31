package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.jobrunr.properties.ApplicationProperties;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.jobrunr.scheduling.RecurringJobBuilder;
import org.jobrunr.scheduling.cron.Cron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

public class ProducerScheduler {

    private static final Logger log = LoggerFactory.getLogger(ProducerScheduler.class);

    private final ApplicationProperties applicationProperties;
    private final JobRequestScheduler jobRequestScheduler;

    public ProducerScheduler(ApplicationProperties applicationProperties,
                             JobRequestScheduler jobRequestScheduler) {
        this.applicationProperties = applicationProperties;
        this.jobRequestScheduler = jobRequestScheduler;
    }

    @EventListener(ApplicationReadyEvent.class)
    void schedule() {
        log.info("Producer scheduling is enabled");
        var produce = applicationProperties.getProduce();
        var job = RecurringJobBuilder.aRecurringJob()
                .withCron(Cron.every15seconds())
                .withName("Produce fibonacci numbers")
                .withJobRequest(new ProduceJobRequest(produce.getLimit(), produce.getAlgorithm()))
                .withLabels("producer")
                .withAmountOfRetries(3);
        jobRequestScheduler.createRecurrently(job);
    }
}
