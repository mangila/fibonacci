package com.github.mangila.fibonacci.redis;

import com.github.mangila.fibonacci.redis.test.RedisTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataRedisTest
@RedisTestContainer
@Import({DefaultRedisRepository.class, RedisConfig.class})
class DefaultRedisRepositoryTest {

    @Autowired
    private RedisRepository repository;

    @Test
    void longBlockingOperation() {
    }

    @Test
    void tryReserveBloomFilter() {
    }

    @Test
    void addToBloomFilter() {
    }

    @Test
    void addToStream() {
    }

    @Test
    void removeFromStream() {
    }
}