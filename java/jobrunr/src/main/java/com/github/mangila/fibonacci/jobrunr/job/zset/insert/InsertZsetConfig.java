package com.github.mangila.fibonacci.jobrunr.job.zset.insert;

import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.redis.RedisKey;
import com.github.mangila.fibonacci.redis.RedisRepository;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@ConditionalOnProperty(prefix = "app.job.zset.insert", name = "enabled", havingValue = "true")
@Configuration
public class InsertZsetConfig {

    @Bean
    InsertZsetScheduler insertZsetScheduler(InsertZsetProperties properties, JobRequestScheduler jobRequestScheduler) {
        return new InsertZsetScheduler(properties, jobRequestScheduler);
    }

    @Bean
    InsertZsetJobHandler insertZsetJobHandler(
            RedisKey zset,
            JsonMapper jsonMapper,
            PostgresRepository postgresRepository,
            RedisRepository redisRepository
    ) {
        return new InsertZsetJobHandler(zset, jsonMapper, postgresRepository, redisRepository);
    }

}
