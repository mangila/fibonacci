package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.postgres.PostgresRepository;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(prefix = "app.job.producer", name = "enabled", havingValue = "true")
@Configuration
public class ProducerConfig {

    @Bean
    ProducerJobHandler fibonacciProduceJobHandler(PostgresRepository postgresRepository) {
        return new ProducerJobHandler(postgresRepository);
    }

    @Bean
    ProducerScheduler producerScheduler(ProducerProperties properties, JobRequestScheduler jobRequestScheduler) {
        return new ProducerScheduler(properties, jobRequestScheduler);
    }
}
