package com.github.mangila.fibonacci.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Bean("postgresListenerExecutor")
    SimpleAsyncTaskExecutor postgresListenerExecutor() {
        var taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setThreadNamePrefix("postgres-listener");
        taskExecutor.setVirtualThreads(true);
        return taskExecutor;
    }

}
