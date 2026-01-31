package com.github.mangila.fibonacci.jobrunr.job.zset.insert;

import org.jobrunr.scheduling.JobRequestScheduler;
import org.jobrunr.scheduling.RecurringJobBuilder;
import org.jobrunr.scheduling.cron.Cron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

public class InsertZsetScheduler {

    private static final Logger log = LoggerFactory.getLogger(InsertZsetScheduler.class);

    private final InsertZsetProperties properties;
    private final JobRequestScheduler jobRequestScheduler;

    public InsertZsetScheduler(InsertZsetProperties properties,
                               JobRequestScheduler jobRequestScheduler) {
        this.properties = properties;
        this.jobRequestScheduler = jobRequestScheduler;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void schedule() {
        log.info("Zset insert is enabled");
        final var limit = properties.getLimit();
        final var job = RecurringJobBuilder.aRecurringJob()
                .withCron(Cron.every15seconds())
                .withName("Insert to zset")
                .withJobRequest(new InsertZsetJobRequest(limit))
                .withLabels("zset-insert")
                .withAmountOfRetries(3);
        jobRequestScheduler.createRecurrently(job);
    }

}
