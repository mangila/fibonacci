package com.github.mangila.fibonacci.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.UnifiedJedis;

@Configuration
public class RedisConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    @Bean
    UnifiedJedis unifiedJedis(JedisConnectionFactory jedisConnectionFactory) {
        return new JedisPooled(jedisConnectionFactory.getHostName(), jedisConnectionFactory.getPort());
    }

    @Bean
    RedisKey streamKey() {
        return new RedisKey("fibonacci:stream");
    }

    @Bean
    RedisKey queueKey() {
        return new RedisKey("fibonacci:queue");
    }

    @Bean
    RedisKey zsetKey() {
        return new RedisKey("fibonacci:zset");
    }

    @Bean
    RedisKey valueKey() {
        return new RedisKey("fibonacci:value");
    }

    @Bean
    RedisKey bloomFilterKey() {
        return new RedisKey("fibonacci:bloom");
    }

    @Bean
    FunctionName drainZset() {
        return new FunctionName("drain_zset");
    }

    @Bean
    FunctionName addZset() {
        return new FunctionName("add_zset");
    }

}
