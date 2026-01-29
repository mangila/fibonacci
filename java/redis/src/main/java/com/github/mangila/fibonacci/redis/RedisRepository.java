package com.github.mangila.fibonacci.redis;

import io.github.mangila.ensure4j.Ensure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.UnifiedJedis;

import java.util.List;

@Repository
public class RedisRepository {

    private static final Logger log = LoggerFactory.getLogger(RedisRepository.class);

    private final UnifiedJedis jedis;

    public RedisRepository(UnifiedJedis jedis) {
        this.jedis = jedis;
    }

    public String set(RedisKey key, String value) {
        Ensure.notNull(key, "Key must not be null");
        Ensure.notBlank(value, "Value must not be blank");
        return jedis.set(key.value(), value);
    }

    public String createBloomFilter(RedisKey key, double errorRate, int capacity) {
        Ensure.notNull(key, "Key must not be null");
        // TODO: ensure with floats
        Ensure.positive(capacity, "Capacity must be positive");
        return jedis.bfReserve(key.value(), errorRate, capacity);
    }

    public boolean existsInBloomFilter(RedisKey key, String value) {
        Ensure.notNull(key, "Key must not be null");
        Ensure.notBlank(value, "Value must not be blank");
        return jedis.bfExists(key.value(), value);
    }

    public long pushList(RedisKey key, String value) {
        Ensure.notNull(key, "Key must not be null");
        Ensure.notBlank(value, "Value must not be blank");
        return jedis.lpush(key.value(), value);
    }

    public String functionLoad(String code) {
        Ensure.notBlank(code, "Code must not be blank");
        return jedis.functionLoad(code);
    }

    public Object functionCall(FunctionName functionName, List<RedisKey> keys, List<String> args) {
        Ensure.notNull(functionName, "Function name must not be null");
        Ensure.notNull(keys, "Keys must not be null");
        Ensure.notNull(args, "Args must not be null");
        var keysAsString = keys.stream().map(RedisKey::value).toList();
        return jedis.fcall(functionName.value(), keysAsString, args);
    }
}
