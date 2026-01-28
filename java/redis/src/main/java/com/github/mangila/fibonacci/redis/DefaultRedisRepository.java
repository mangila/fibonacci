package com.github.mangila.fibonacci.redis;

import io.github.mangila.ensure4j.Ensure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.UnifiedJedis;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Repository
public class DefaultRedisRepository implements RedisRepository {

    private static final Logger log = LoggerFactory.getLogger(DefaultRedisRepository.class);
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

    public void tryReserveBloomFilter() {
        try {
            var ok = jedis.bfReserve(RedisConfig.BLOOM_FILTER_KEY,
                    RedisConfig.BLOOM_FILTER_ERROR_RATE,
                    RedisConfig.BLOOM_FILTER_CAPACITY);
            log.info("Bloom filter reserved: {}", ok);
        } catch (Exception e) {
            log.warn("Bloom filter already exists");
        }
    }

    public boolean addToBloomFilter(String value) {
        Ensure.notBlank(value, "Value must not be blank");
        return jedis.bfAdd(RedisConfig.BLOOM_FILTER_KEY, value);
    }

    @Override
    public StreamEntryID addToStream(int sequence, Map<String, String> data) {
        Ensure.positive(sequence);
        Ensure.notEmpty(data, "Data must not be empty");
        return jedis.xadd(RedisConfig.STREAM_KEY, new StreamEntryID(sequence, sequence), data);
    }

    @Override
    public void removeFromStream(List<StreamEntryID> ids) {
        Ensure.notEmpty(ids);
        jedis.xdel(RedisConfig.STREAM_KEY, ids.toArray(StreamEntryID[]::new));
    }
}
