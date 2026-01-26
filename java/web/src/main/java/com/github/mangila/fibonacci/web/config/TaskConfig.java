package com.github.mangila.fibonacci.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class TaskConfig {

    @Bean
    SimpleAsyncTaskExecutor ioAsyncTaskExecutor() {
        var executor = new SimpleAsyncTaskExecutor();
        executor.setThreadNamePrefix("io-task-");
        executor.setVirtualThreads(true);
        return executor;
    }
}
