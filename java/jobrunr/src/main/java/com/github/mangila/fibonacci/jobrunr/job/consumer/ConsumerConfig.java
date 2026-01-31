package com.github.mangila.fibonacci.jobrunr.job.consumer;

import com.github.mangila.fibonacci.jobrunr.job.consumer.compute.ComputeScheduler;
import com.github.mangila.fibonacci.redis.RedisBootstrap;
import com.github.mangila.fibonacci.redis.RedisKey;
import com.github.mangila.fibonacci.redis.RedisRepository;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@ConditionalOnProperty(prefix = "app.job.consumer", name = "enabled", havingValue = "true")
@Configuration
public class ConsumerConfig {

    @Bean
    ConsumerBootstrap consumerBootstrap(RedisKey bloomFilter, RedisBootstrap redisBootstrap) {
        return new ConsumerBootstrap(bloomFilter, redisBootstrap);
    }

    @Bean
    ConsumerJobHandler consumerJobHandler(
            JsonMapper jsonMapper,
            ComputeScheduler computeScheduler,
            RedisKey queue,
            RedisKey bloomFilter,
            RedisRepository redisRepository
    ) {
        return new ConsumerJobHandler(jsonMapper, computeScheduler, queue, bloomFilter, redisRepository);
    }

    @Bean
    ConsumerScheduler consumerScheduler(ConsumerProperties properties, JobRequestScheduler jobRequestScheduler) {
        return new ConsumerScheduler(properties, jobRequestScheduler);
    }
}
