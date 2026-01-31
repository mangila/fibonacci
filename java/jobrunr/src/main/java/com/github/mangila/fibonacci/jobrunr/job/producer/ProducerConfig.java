package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.jobrunr.properties.ApplicationProperties;
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
import tools.jackson.databind.json.JsonMapper;

@ConditionalOnProperty(prefix = "app.produce", name = "enabled", havingValue = "true")
@Configuration
public class ProducerConfig {

    @Bean
    ProduceJobHandler fibonacciProduceJobHandler(JsonMapper jsonMapper,
                                                 JedisConnectionFactory jedisConnectionFactory,
                                                 FunctionName produceSequence,
                                                 RedisKey bloomFilter,
                                                 RedisKey queue
    ) {
        return new ProduceJobHandler(jsonMapper, jedisConnectionFactory, produceSequence, bloomFilter, queue);
    }

    @Bean
    ProducerBootstrap producerBootstrap(RedisKey bloomFilter,
                                        RedisBootstrap redisBootstrap,
                                        @Value("classpath:/functions/produce_sequence.lua") Resource produceSequenceScript) {
        return new ProducerBootstrap(bloomFilter, redisBootstrap, produceSequenceScript);
    }

    @Bean
    ProducerScheduler producerScheduler(ApplicationProperties applicationProperties, JobRequestScheduler jobRequestScheduler) {
        return new ProducerScheduler(applicationProperties, jobRequestScheduler);
    }

}
