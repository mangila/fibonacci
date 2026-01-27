package com.github.mangila.fibonacci.scheduler.jobrunr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class JobRunrSchedulerWatcher implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(JobRunrSchedulerWatcher.class);

    private final SimpleAsyncTaskExecutor schedulerThreadExecutor;
    private final JobRunrScheduler jobRunrScheduler;
    private CompletableFuture<Void> future;

    public JobRunrSchedulerWatcher(SimpleAsyncTaskExecutor schedulerThreadExecutor,
                                   JobRunrScheduler jobRunrScheduler) {
        this.schedulerThreadExecutor = schedulerThreadExecutor;
        this.jobRunrScheduler = jobRunrScheduler;
    }

    @Override
    public void start() {
        log.info("Starting JobRunr scheduler");
        this.future = schedulerThreadExecutor.submitCompletable(jobRunrScheduler);
    }

    @Override
    public void stop() {
        log.info("Stopping JobRunr scheduler");
        jobRunrScheduler.stop();
        future.cancel(true);
    }

    @Override
    public boolean isRunning() {
        return jobRunrScheduler.isRunning();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
