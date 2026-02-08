package com.github.mangila.fibonacci.jobrunr.job.zset.drain;

import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.redis.FunctionName;
import com.github.mangila.fibonacci.redis.RedisKey;
import com.github.mangila.fibonacci.redis.RedisRepository;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.support.TransactionTemplate;
import redis.clients.jedis.JedisPooled;

@ConditionalOnProperty(prefix = "app.job.zset.drain", name = "enabled", havingValue = "true")
@Configuration
public class DrainZsetConfig {

    @Bean
    DrainZsetBootstrap drainZsetBootstrap(RedisRepository redisRepository,
                                          @Value("classpath:functions/drain_zset.lua") Resource drainZsetScript) {
        return new DrainZsetBootstrap(redisRepository, drainZsetScript);
    }

    @Bean
    DrainZsetJobHandler drainZsetJobHandler(
            JedisPooled jedis,
            PostgresRepository postgresRepository,
            TransactionTemplate transactionTemplate,
            RedisKey zset,
            RedisKey stream,
            RedisKey value,
            FunctionName drainZset
    ) {
        return new DrainZsetJobHandler(
                jedis,
                postgresRepository,
                transactionTemplate,
                zset,
                stream,
                value,
                drainZset
        );
    }

    @Bean
    DrainZsetScheduler drainZsetScheduler(DrainZsetProperties properties, JobRequestScheduler jobRequestScheduler) {
        return new DrainZsetScheduler(properties, jobRequestScheduler);
    }
}
