package com.github.mangila.fibonacci.jobrunr.job.zset;

import com.github.mangila.fibonacci.jobrunr.properties.ApplicationProperties;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.jobrunr.scheduling.RecurringJobBuilder;
import org.jobrunr.scheduling.cron.Cron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(prefix = "app.zset.insert", name = "enabled", havingValue = "true")
@Service
public class ZsetScheduler {

    private static final Logger log = LoggerFactory.getLogger(ZsetScheduler.class);

    private final ApplicationProperties applicationProperties;
    private final JobRequestScheduler jobRequestScheduler;

    public ZsetScheduler(ApplicationProperties applicationProperties,
                         JobRequestScheduler jobRequestScheduler) {
        this.applicationProperties = applicationProperties;
        this.jobRequestScheduler = jobRequestScheduler;
    }

    @EventListener(ApplicationReadyEvent.class)
    void schedule() {
        var zset = applicationProperties.getZset();
        log.info("Scheduling: {}", InsertRedisZsetJobRequest.class.getSimpleName());
        var job = RecurringJobBuilder.aRecurringJob()
                .withCron(Cron.every15seconds())
                .withName("Insert to zset")
                .withJobRequest(new InsertRedisZsetJobRequest(zset.getInsert().getLimit()))
                .withLabels("zset-insert")
                .withAmountOfRetries(3);
        jobRequestScheduler.createRecurrently(job);
        log.info("Scheduling: {}", DrainZsetJobRequest.class.getSimpleName());
        job = RecurringJobBuilder.aRecurringJob()
                .withCron(Cron.every15seconds())
                .withName("Drain zset")
                .withJobRequest(new DrainZsetJobRequest(zset.getDrain().getLimit()))
                .withLabels("zset-drain")
                .withAmountOfRetries(3);
        jobRequestScheduler.createRecurrently(job);
    }
}
