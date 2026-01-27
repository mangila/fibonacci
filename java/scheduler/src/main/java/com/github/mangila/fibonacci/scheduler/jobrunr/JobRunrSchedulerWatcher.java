package com.github.mangila.fibonacci.scheduler.jobrunr;

import org.springframework.context.SmartLifecycle;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class JobRunrSchedulerWatcher implements SmartLifecycle {

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
        this.future = schedulerThreadExecutor.submitCompletable(jobRunrScheduler);
    }

    @Override
    public void stop() {
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
