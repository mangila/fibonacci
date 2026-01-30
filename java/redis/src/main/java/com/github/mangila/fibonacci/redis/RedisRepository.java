package com.github.mangila.fibonacci.redis;

import io.github.mangila.ensure4j.Ensure;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.params.ZAddParams;

import java.util.List;

@Repository
public class RedisRepository {

    private static final Logger log = LoggerFactory.getLogger(RedisRepository.class);

    private final UnifiedJedis jedis;

    public RedisRepository(UnifiedJedis jedis) {
        this.jedis = jedis;
    }

    public String createBloomFilter(RedisKey key, double errorRate, int capacity) {
        Ensure.notNull(key, "Key must not be null");
        // TODO: ensure with floats
        Ensure.positive(capacity, "Capacity must be positive");
        return jedis.bfReserve(key.value(), errorRate, capacity);
    }

    public long addZset(RedisKey key, int score, String member) {
        Ensure.notNull(key, "Key must not be null");
        Ensure.positive(score, "Score must be positive");
        Ensure.notBlank(member, "Member must not be blank");
        return jedis.zadd(key.value(),
                score,
                member,
                ZAddParams.zAddParams().nx());
    }

    public String functionLoad(String code) {
        // TODO: ensure string start with and end with
        // Ensure.startsWith("#!lua name=", code);
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

    public void flushAll() {
        jedis.flushAll();
    }

    public void functionFlush() {
        jedis.functionFlush();
    }

    public void flushEverything() {
        flushAll();
        functionFlush();
    }

    @Nullable
    public String popQueue(RedisKey key) {
        return jedis.lpop(key.value());
    }

    public boolean addBloomFilter(RedisKey key, int sequence) {
        return jedis.bfAdd(key.value(), String.valueOf(sequence));
    }

    public boolean checkBloomFilter(RedisKey key, int sequence) {
        return jedis.bfExists(key.value(), String.valueOf(sequence));
    }
}
