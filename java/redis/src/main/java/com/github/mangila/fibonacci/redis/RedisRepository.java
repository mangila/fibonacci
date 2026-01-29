package com.github.mangila.fibonacci.redis;

import io.github.mangila.ensure4j.Ensure;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.UnifiedJedis;

import java.util.List;
import java.util.Map;

@Repository
public class RedisRepository {

    private static final Logger log = LoggerFactory.getLogger(RedisRepository.class);

    private final UnifiedJedis jedis;

    public RedisRepository(UnifiedJedis jedis) {
        this.jedis = jedis;
    }

    public void tryCreateBloomFilter(String key, double errorRate, int capacity) {
        Ensure.notBlank(key, "Key must not be blank");
        // TODO: ensure with floats
        Ensure.positive(capacity, "Capacity must be positive");
        try {
            var ok = jedis.bfReserve(key, errorRate, capacity);
            log.info("Bloom filter reserved: {}", ok);
        } catch (Exception e) {
            log.warn("Bloom filter already exists");
        }
    }

    public long pushList(String key, String value) {
        return jedis.lpush(key, value);
    }

    public List<String> blockingPopList(int timeout, String key) {
        return jedis.blpop(timeout, key);
    }

    public boolean bloomFilterAdd(String key, String value) {
        Ensure.notBlank(key, "Key must not be blank");
        Ensure.notBlank(value, "Value must not be blank");
        return jedis.bfAdd(key, value);
    }

    @Nullable
    public String getValue(String key) {
        Ensure.notBlank(key, "Key must not be blank");
        return jedis.get(key);
    }

    public StreamEntryID streamAdd(String key, int sequence, Map<String, String> data) {
        Ensure.positive(sequence);
        Ensure.notEmpty(data, "Data must not be empty");
        return jedis.xadd(RedisConfig.STREAM_KEY, new StreamEntryID(sequence, sequence), data);
    }

    public long sortedSetAdd(String key, double score, String member) {
        Ensure.notBlank(key, "Key must not be blank");
        // TODO: ensure with floats
        Ensure.notBlank(member, "Member must not be blank");
        return jedis.zadd(key, score, member);
    }

    public String functionLoad(String code) {
        Ensure.notBlank(code, "Code must not be blank");
        jedis.functionFlush();
        return jedis.functionLoad(code);
    }

    public Object functionCall(String functionName, List<String> keys, List<String> args) {
        return jedis.fcall(functionName, keys, args);
    }
}
