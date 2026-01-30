package com.github.mangila.fibonacci.jobrunr.job.consumer;

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

@ConditionalOnProperty(prefix = "app.consume", name = "enabled", havingValue = "true")
@Service
public class ConsumerScheduler {

    private static final Logger log = LoggerFactory.getLogger(ConsumerScheduler.class);
    private final ApplicationProperties applicationProperties;
    private final JobRequestScheduler jobRequestScheduler;

    public ConsumerScheduler(ApplicationProperties applicationProperties, JobRequestScheduler jobRequestScheduler) {
        this.applicationProperties = applicationProperties;
        this.jobRequestScheduler = jobRequestScheduler;
    }

    @EventListener(ApplicationReadyEvent.class)
    void schedule() {
        var consume = applicationProperties.getConsume();
        log.info("Scheduling: {}", FibonacciConsumeJobRequest.class.getSimpleName());
        var job = RecurringJobBuilder.aRecurringJob()
                .withCron(Cron.every15seconds())
                .withName("Consume fibonacci numbers")
                .withJobRequest(new FibonacciConsumeJobRequest(consume.getLimit()))
                .withLabels("consumer")
                .withAmountOfRetries(3);
        jobRequestScheduler.createRecurrently(job);
    }
}
