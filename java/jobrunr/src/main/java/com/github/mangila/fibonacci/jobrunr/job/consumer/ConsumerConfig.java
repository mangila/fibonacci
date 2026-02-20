package com.github.mangila.fibonacci.jobrunr.job.consumer;

import com.github.mangila.fibonacci.jobrunr.job.consumer.compute.ComputeScheduler;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(prefix = "app.job.consumer", name = "enabled", havingValue = "true")
@Configuration
public class ConsumerConfig {

    @Bean
    ConsumerJobHandler consumerJobHandler(ComputeScheduler computeScheduler, PostgresRepository postgresRepository) {
        return new ConsumerJobHandler(computeScheduler, postgresRepository);
    }

    @Bean
    ConsumerScheduler consumerScheduler(ConsumerProperties properties, JobRequestScheduler jobRequestScheduler) {
        return new ConsumerScheduler(properties, jobRequestScheduler);
    }
}
