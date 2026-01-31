package com.github.mangila.fibonacci.jobrunr.job.zset.drain;

import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.redis.FunctionName;
import com.github.mangila.fibonacci.redis.RedisBootstrap;
import com.github.mangila.fibonacci.redis.RedisKey;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.transaction.support.TransactionTemplate;

@ConditionalOnProperty(prefix = "app.job.zset.drain", name = "enabled", havingValue = "true")
@Configuration
public class DrainZsetConfig {

    @Bean
    DrainZsetBootstrap drainZsetBootstrap(RedisBootstrap redisBootstrap,
                                          @Value("classpath:functions/drain_zset.lua") Resource drainZsetScript) {
        return new DrainZsetBootstrap(redisBootstrap, drainZsetScript);
    }

    @Bean
    DrainZsetJobHandler drainZsetJobHandler(
            JedisConnectionFactory jedisConnectionFactory,
            PostgresRepository postgresRepository,
            TransactionTemplate transactionTemplate,
            RedisKey zset,
            RedisKey stream,
            RedisKey value,
            FunctionName drainZset
    ) {
        return new DrainZsetJobHandler(
                jedisConnectionFactory,
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
