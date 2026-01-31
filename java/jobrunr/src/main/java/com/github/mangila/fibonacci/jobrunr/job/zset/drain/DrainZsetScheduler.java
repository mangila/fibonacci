package com.github.mangila.fibonacci.jobrunr.job.zset.drain;

import org.jobrunr.scheduling.JobRequestScheduler;
import org.jobrunr.scheduling.RecurringJobBuilder;
import org.jobrunr.scheduling.cron.Cron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

public class DrainZsetScheduler {

    private static final Logger log = LoggerFactory.getLogger(DrainZsetScheduler.class);

    private final DrainZsetProperties properties;
    private final JobRequestScheduler jobRequestScheduler;

    public DrainZsetScheduler(DrainZsetProperties properties,
                              JobRequestScheduler jobRequestScheduler) {
        this.properties = properties;
        this.jobRequestScheduler = jobRequestScheduler;
    }

    @EventListener(ApplicationReadyEvent.class)
    void schedule() {
        log.info("Zset drain is enabled");
        final var limit = properties.getLimit();
        final var job = RecurringJobBuilder.aRecurringJob()
                .withCron(Cron.every15seconds())
                .withName("Drain zset")
                .withJobRequest(new DrainZsetJobRequest(limit))
                .withLabels("zset-drain")
                .withAmountOfRetries(3);
        jobRequestScheduler.createRecurrently(job);
    }

}
