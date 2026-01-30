package com.github.mangila.fibonacci.jobrunr.scheduler;

import com.github.mangila.fibonacci.jobrunr.job.DrainZsetJobRequest;
import com.github.mangila.fibonacci.jobrunr.job.FibonacciConsumeJobRequest;
import com.github.mangila.fibonacci.jobrunr.job.FibonacciProduceJobRequest;
import com.github.mangila.fibonacci.jobrunr.job.InsertRedisZsetJobRequest;
import com.github.mangila.fibonacci.jobrunr.properties.ApplicationProperties;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Schedules the Fibonacci computation job using JobRunr.
 */
@Service
public class JobRunrScheduler {

    private static final Logger log = LoggerFactory.getLogger(JobRunrScheduler.class);

    private final ApplicationProperties applicationProperties;
    private final JobRequestScheduler jobRequestScheduler;

    public JobRunrScheduler(ApplicationProperties applicationProperties,
                            JobRequestScheduler jobRequestScheduler) {
        this.applicationProperties = applicationProperties;
        this.jobRequestScheduler = jobRequestScheduler;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        var produce = applicationProperties.getProduce();
        var consume = applicationProperties.getConsume();
        var zset = applicationProperties.getZset();
        if (produce.isEnabled()) {
            log.info("Produce job enabled, starting recurring job: FibonacciProduceJobRequest");
            jobRequestScheduler.scheduleRecurrently(
                    Cron.every15seconds(),
                    new FibonacciProduceJobRequest(produce.getLimit())
            );
        }
        if (consume.isEnabled()) {
            log.info("Consume job enabled, starting recurring job: FibonacciConsumeJobRequest");
            jobRequestScheduler.scheduleRecurrently(
                    Cron.every15seconds(),
                    new FibonacciConsumeJobRequest(consume.getLimit())
            );
        }
        if (zset.getInsert().isEnabled()) {
            log.info("Insert job enabled, starting recurring job: InsertRedisZsetJobRequest");
            jobRequestScheduler.scheduleRecurrently(
                    Cron.every15seconds(),
                    new InsertRedisZsetJobRequest(zset.getInsert().getLimit())
            );
        }
        if (zset.getDrain().isEnabled()) {
            log.info("Drain job enabled, starting recurring job: DrainZsetJobRequest");
            jobRequestScheduler.scheduleRecurrently(
                    Cron.every15seconds(),
                    new DrainZsetJobRequest(zset.getDrain().getLimit())
            );
        }
    }
}
