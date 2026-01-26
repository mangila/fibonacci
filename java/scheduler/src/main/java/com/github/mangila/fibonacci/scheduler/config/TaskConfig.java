package com.github.mangila.fibonacci.scheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TaskConfig {
    @Bean
    ThreadPoolTaskExecutor computeAsyncTaskExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("compute-task-");
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Bean
    SimpleAsyncTaskExecutor simpleAsyncTaskExecutor() {
        var executor = new SimpleAsyncTaskExecutor("simple-task-");
        executor.setVirtualThreads(true);
        return executor;
    }
}
