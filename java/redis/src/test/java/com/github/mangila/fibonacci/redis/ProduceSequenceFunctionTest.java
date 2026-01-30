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
public class ProduceSequenceFunctionTest {

    @Autowired
    private FunctionName produceSequence;

    @Autowired
    private RedisRepository repository;

    @Autowired
    private UnifiedJedis jedis;

    @Autowired
    private RedisKey queue;

    @Autowired
    private RedisKey bloomFilter;

    @Value("classpath:functions/produce_sequence.lua")
    private Resource luaScript;

    private List<RedisKey> keys;

    @BeforeEach
    void setUp() throws IOException {
        this.keys = List.of(queue, bloomFilter);
        repository.functionLoad(luaScript.getContentAsString(StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        repository.flushAll();
    }

    @Test
    void test() {
        var sequence = "100";
        var payload = "hello";
        var args = List.of(sequence, payload);
        var result = repository.functionCall(produceSequence, keys, args);
        assertThat(result).isEqualTo("OK: %s".formatted(sequence));
        result = repository.functionCall(produceSequence, keys, args);
        assertThat(result).isEqualTo("EXISTS: %s".formatted(sequence));
        assertThat(jedis.rpop(queue.value())).isEqualTo(payload);
    }
}
