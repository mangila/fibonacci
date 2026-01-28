package com.github.mangila.fibonacci.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface RedisRepository {

    /**
     * Executes a long blocking operation on Redis.
     * We don't want to use a connection from the pool, with has it's own set of connection/socket config.
     * Here we want a custom config connection.
     */
    void longBlockingOperation(Consumer<Jedis> consumer);

    void tryReserveBloomFilter();

    boolean addToBloomFilter(String value);

    StreamEntryID addToStream(int sequence, Map<String, String> data);

    void removeFromStream(List<StreamEntryID> ids);
}
