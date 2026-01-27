package com.github.mangila.fibonacci.redis;

import redis.clients.jedis.Jedis;

import java.util.function.Consumer;

public interface RedisRepository {

    void longBlockingOperation(Consumer<Jedis> consumer);

    void reserveBloomFilter(int errorRate, int capacity);

    void addToBloomFilter(String value);
}
