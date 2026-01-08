package com.github.mangila.fibonacci.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class TaskConfig {

    @Bean
    SimpleAsyncTaskExecutor simpleAsyncTaskExecutor() {
        var executor = new SimpleAsyncTaskExecutor();
        executor.setThreadNamePrefix("task-");
        executor.setVirtualThreads(true);
        executor.setConcurrencyLimit(-1);
        return executor;
    }

}
