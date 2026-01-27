package com.github.mangila.fibonacci.redis;

import io.github.mangila.ensure4j.Ensure;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.UnifiedJedis;

import java.util.function.Consumer;

@Repository
public class DefaultRedisRepository implements RedisRepository {

    private final JedisConnectionFactory jedisConnectionFactory;
    private final UnifiedJedis jedis;

    public DefaultRedisRepository(JedisConnectionFactory jedisConnectionFactory,
                                  UnifiedJedis jedis) {
        this.jedisConnectionFactory = jedisConnectionFactory;
        this.jedis = jedis;
    }

    public void longBlockingOperation(Consumer<Jedis> consumer) {
        Ensure.notNull(consumer, "Consumer must not be null");
        final String host = jedisConnectionFactory.getHostName();
        final int port = jedisConnectionFactory.getPort();
        try (var jedis = new Jedis(host, port, 5000, 0)) {
            consumer.accept(jedis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void reserveBloomFilter(int errorRate, int capacity) {
        Ensure.positive(errorRate, "Error rate must be positive");
        Ensure.positive(capacity, "Capacity must be positive");
        jedis.bfReserve(RedisConfig.BLOOM_FILTER_KEY, errorRate, capacity);
    }

    public void addToBloomFilter(String value) {
        Ensure.notBlank(value, "Value must not be blank");
        jedis.bfAdd(RedisConfig.BLOOM_FILTER_KEY, value);
    }
}
