package com.github.mangila.fibonacci.jobrunr.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Watches the JobRunr scheduler and ensures it runs as a background task.
 */
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
        jobRunrScheduler.setRunning(true);
        this.future = schedulerThreadExecutor.submitCompletable(jobRunrScheduler);
    }

    @Override
    public void stop() {
        log.info("Stopping JobRunr scheduler");
        jobRunrScheduler.setRunning(false);
        future.cancel(true);
        try {
            future.join();
        } catch (Exception e) {
            log.error("Shutdown JobRunr scheduler", e);
        }
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
