package com.github.mangila.fibonacci.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TaskConfig {

    @Bean("ioAsyncTaskExecutor")
    SimpleAsyncTaskExecutor ioAsyncTaskExecutor() {
        var executor = new SimpleAsyncTaskExecutor();
        executor.setThreadNamePrefix("io-task-");
        executor.setVirtualThreads(true);
        return executor;
    }

    @Bean("computeAsyncTaskExecutor")
    ThreadPoolTaskExecutor computeAsyncTaskExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("compute-task-");
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        executor.setQueueCapacity(100);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Bean("postgresListenerExecutor")
    SimpleAsyncTaskExecutor postgresListenerExecutor() {
        var executor = new SimpleAsyncTaskExecutor("postgres-notification-listener");
        executor.setVirtualThreads(true);
        return executor;
    }
}
