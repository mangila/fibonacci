package com.github.mangila.fibonacci.web.sse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

@Configuration
public class SseConfig {

    @Bean("sseTaskScheduler")
    SimpleAsyncTaskScheduler sseTaskScheduler() {
        var scheduler = new SimpleAsyncTaskScheduler();
        scheduler.setThreadNamePrefix("sse-scheduler-");
        scheduler.setVirtualThreads(true);
        return scheduler;
    }
}
