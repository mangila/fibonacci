package com.github.mangila.fibonacci.jobrunr.job.consumer.compute;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ComputeConfig {

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
}
