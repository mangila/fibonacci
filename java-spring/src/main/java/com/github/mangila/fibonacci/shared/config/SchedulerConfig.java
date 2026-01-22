package com.github.mangila.fibonacci.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Bean("fibonacciTaskScheduler")
    SimpleAsyncTaskScheduler fibonacciTaskScheduler() {
        var scheduler = new SimpleAsyncTaskScheduler();
        scheduler.setThreadNamePrefix("fibonacci-scheduler-");
        scheduler.setVirtualThreads(true);
        return scheduler;
    }


    @Bean("sseTaskScheduler")
    SimpleAsyncTaskScheduler sseTaskScheduler() {
        var scheduler = new SimpleAsyncTaskScheduler();
        scheduler.setThreadNamePrefix("sse-scheduler-");
        scheduler.setVirtualThreads(true);
        return scheduler;
    }
}
