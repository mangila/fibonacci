package com.github.mangila.fibonacci.redis;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface RedisRepository {

    void longBlockingOperation(Consumer<Jedis> consumer);

    void tryReserveBloomFilter();

    boolean addToBloomFilter(String value);

    String addToStream(int sequence, Map<String, String> data);

    void removeFromStream(List<String> redisStreamIds);
}
