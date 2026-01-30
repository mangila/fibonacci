package com.github.mangila.fibonacci.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.UnifiedJedis;

@Configuration
public class RedisConfig {

    /**
     * Use with more advanced commands for Redis that Spring template does not provide
     */
    @Bean
    UnifiedJedis unifiedJedis(JedisConnectionFactory jedisConnectionFactory) {
        RedisStandaloneConfiguration config = jedisConnectionFactory.getStandaloneConfiguration();
        return new JedisPooled(
                new HostAndPort(config.getHostName(), config.getPort()),
                DefaultJedisClientConfig.builder()
                        .connectionTimeoutMillis(2000)
                        .socketTimeoutMillis(2000)
                        .database(config.getDatabase())
                        .build()
        );
    }

    @Bean
    RedisKey stream() {
        return new RedisKey("fibonacci:stream");
    }

    @Bean
    RedisKey queue() {
        return new RedisKey("fibonacci:queue");
    }

    @Bean
    RedisKey zset() {
        return new RedisKey("fibonacci:zset");
    }

    @Bean
    RedisKey value() {
        return new RedisKey("fibonacci:value");
    }

    @Bean
    RedisKey bloomFilter() {
        return new RedisKey("fibonacci:bloom");
    }

    @Bean
    FunctionName produceSequence() {
        return new FunctionName("produce_sequence");
    }

    @Bean
    FunctionName drainZset() {
        return new FunctionName("drain_zset");
    }
}
