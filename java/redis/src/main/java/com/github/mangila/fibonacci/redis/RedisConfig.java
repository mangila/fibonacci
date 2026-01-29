package com.github.mangila.fibonacci.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.UnifiedJedis;

@Configuration
public class RedisConfig {

    public static final String STREAM_KEY = "fibonacci::stream";
    public static final String QUEUE_KEY = "fibonacci::queue";
    public static final String ZSET_KEY = "fibonacci::zset";
    public static final String VALUE_KEY = "fibonacci::value";
    public static final String BLOOM_FILTER_KEY = "fibonacci::bloom";
    public static final String FUNCTION_NAME = "drain_zset";

    @Bean
    UnifiedJedis unifiedJedis(JedisConnectionFactory jedisConnectionFactory) {
        return new JedisPooled(jedisConnectionFactory.getHostName(), jedisConnectionFactory.getPort());
    }
}
