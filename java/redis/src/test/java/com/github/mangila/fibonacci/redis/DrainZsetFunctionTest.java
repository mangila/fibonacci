package com.github.mangila.fibonacci.redis;

import com.github.mangila.fibonacci.redis.test.RedisTestContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import redis.clients.jedis.UnifiedJedis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataRedisTest
@RedisTestContainer
@Import({RedisRepository.class,
        RedisConfig.class})
public class DrainZsetFunctionTest {

    @Autowired
    private UnifiedJedis jedis;

    @Autowired
    private RedisRepository repository;

    @Autowired
    private FunctionName drainZset;

    @Autowired
    private RedisKey zset;

    @Autowired
    private RedisKey stream;

    @Autowired
    private RedisKey value;

    @Value("classpath:functions/drain_zset.lua")
    private Resource luaScript;

    private List<RedisKey> keys;

    @BeforeEach
    void setUp() throws IOException {
        this.keys = List.of(zset, stream, value);
        repository.functionLoad(luaScript.getContentAsString(StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        repository.flushAll();
    }

    @Test
    void test() {
        assertThat(jedis.get(value.value())).isNull();
        var result = repository.functionCall(drainZset, keys, List.of());
        assertThat(result).isEqualTo("ZSET_EMPTY");
        repository.addZset(zset, 2, "member2");
        result = repository.functionCall(drainZset, keys, List.of());
        assertThat(result).isEqualTo("SEQUENCE_MISMATCH: 2:1");
        repository.addZset(zset, 1, "member1");
        result = repository.functionCall(drainZset, keys, List.of());
        assertThat(result).isEqualTo("1");
        assertThat(jedis.zcard(zset.value())).isEqualTo(1);
        assertThat(jedis.xlen(stream.value())).isEqualTo(1);
        assertThat(jedis.get(value.value())).isEqualTo("2");
    }
}