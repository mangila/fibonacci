package com.github.mangila.fibonacci.jobrunr.job.consumer.compute;

import com.github.mangila.fibonacci.postgres.PostgresRepository;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.support.TransactionTemplate;

@ConditionalOnProperty(prefix = "app.job.consumer", name = "enabled", havingValue = "true")
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

    @Bean
    ComputeHandler computeHandler(ThreadPoolTaskExecutor computeAsyncTaskExecutor,
                                  PostgresRepository postgresRepository,
                                  TransactionTemplate transactionTemplate) {
        return new ComputeHandler(computeAsyncTaskExecutor, postgresRepository, transactionTemplate);
    }

    @Bean
    ComputeScheduler computeScheduler(JobRequestScheduler jobRequestScheduler) {
        return new ComputeScheduler(jobRequestScheduler);
    }
}
