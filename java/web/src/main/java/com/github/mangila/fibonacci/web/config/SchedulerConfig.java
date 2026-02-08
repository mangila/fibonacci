package com.github.mangila.fibonacci.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Bean("ioAsyncTaskExecutor")
    SimpleAsyncTaskExecutor ioAsyncTaskExecutor() {
        var scheduler = new SimpleAsyncTaskScheduler();
        scheduler.setThreadNamePrefix("sse-scheduler-");
        scheduler.setVirtualThreads(true);
        return scheduler;
    }
}
