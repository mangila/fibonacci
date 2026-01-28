package com.github.mangila.fibonacci.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.UnifiedJedis;

@Configuration
public class RedisConfig {

    public static final String STREAM_KEY = "fibonacci::stream";
    public static final String SEQUENCE_QUEUE_KEY = "fibonacci::sequence";
    public static final String BLOOM_FILTER_KEY = "fibonacci::bloom";
    public static final int BLOOM_FILTER_CAPACITY = 1000000;
    public static final double BLOOM_FILTER_ERROR_RATE = 0.001;

    @Bean
    UnifiedJedis unifiedJedis(JedisConnectionFactory jedisConnectionFactory) {
        return new JedisPooled(jedisConnectionFactory.getHostName(), jedisConnectionFactory.getPort());
    }
}
