package com.github.mangila.fibonacci.scheduler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

@EnableScheduling
@Configuration
public class SchedulerConfig {

    @Bean("simpleAsyncTaskScheduler")
    SimpleAsyncTaskScheduler simpleAsyncTaskScheduler() {
        var scheduler = new SimpleAsyncTaskScheduler();
        scheduler.setThreadNamePrefix("scheduler-");
        scheduler.setVirtualThreads(true);
        return scheduler;
    }

}
