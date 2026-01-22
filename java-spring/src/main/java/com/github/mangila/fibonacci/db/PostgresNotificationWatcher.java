package com.github.mangila.fibonacci.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PostgresNotificationWatcher implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(PostgresNotificationWatcher.class);

    private final SimpleAsyncTaskExecutor postgresExecutor;
    private final PostgresNotificationListener listener;
    private CompletableFuture<Void> runningTask;

    public PostgresNotificationWatcher(SimpleAsyncTaskExecutor postgresExecutor,
                                       PostgresNotificationListener listener) {
        this.postgresExecutor = postgresExecutor;
        this.listener = listener;
    }

    @Override
    public void start() {
        log.info("Starting Postgres notification listener");
        this.runningTask = postgresExecutor.submitCompletable(listener);
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void stop() {
        log.info("Stopping Postgres notification listener");
        listener.stop();
        runningTask.cancel(true);

        try {
            runningTask.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.info("Postgres notification listener shutdown successfully");
        }
    }

    @Override
    public boolean isRunning() {
        return listener.isRunning();
    }
}
