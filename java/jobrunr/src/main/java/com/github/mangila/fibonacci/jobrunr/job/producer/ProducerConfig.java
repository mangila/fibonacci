package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.redis.FunctionName;
import com.github.mangila.fibonacci.redis.RedisBootstrap;
import com.github.mangila.fibonacci.redis.RedisKey;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import redis.clients.jedis.JedisPooled;
import tools.jackson.databind.json.JsonMapper;

@ConditionalOnProperty(prefix = "app.job.producer", name = "enabled", havingValue = "true")
@Configuration
public class ProducerConfig {

    @Bean
    ProducerJobHandler fibonacciProduceJobHandler(JsonMapper jsonMapper,
                                                  JedisPooled jedis,
                                                  FunctionName produceSequence,
                                                  RedisKey bloomFilter,
                                                  RedisKey queue
    ) {
        return new ProducerJobHandler(jsonMapper, jedis, produceSequence, bloomFilter, queue);
    }

    @Bean
    ProducerBootstrap producerBootstrap(RedisKey bloomFilter,
                                        RedisBootstrap redisBootstrap,
                                        @Value("classpath:functions/produce_sequence.lua") Resource produceSequenceScript) {
        return new ProducerBootstrap(bloomFilter, redisBootstrap, produceSequenceScript);
    }

    @Bean
    ProducerScheduler producerScheduler(ProducerProperties properties, JobRequestScheduler jobRequestScheduler) {
        return new ProducerScheduler(properties, jobRequestScheduler);
    }

}
