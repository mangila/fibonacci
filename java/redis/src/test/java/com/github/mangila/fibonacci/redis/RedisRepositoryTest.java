package com.github.mangila.fibonacci.redis;

import com.github.mangila.fibonacci.redis.test.RedisTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest;
import org.springframework.context.annotation.Import;

@DataRedisTest
@RedisTestContainer
@Import({RedisRepository.class, RedisConfig.class})
class RedisRepositoryTest {

    @Autowired
    private RedisRepository repository;

    @Test
    void tryCreateBloomFilter() {
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