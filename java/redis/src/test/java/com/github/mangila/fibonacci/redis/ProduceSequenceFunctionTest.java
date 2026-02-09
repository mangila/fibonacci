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
        // given
        var sequence = "100";
        var payload = "hello";
        var args = List.of(sequence, payload);
        repository.createBloomFilter(bloomFilter,
                RedisConfig.DEFAULT_BLOOM_FILTER_ERROR_RATE,
                RedisConfig.DEFAULT_BLOOM_FILTER_CAPACITY);
        // then
        var result = repository.functionCall(produceSequence, keys, args);
        // assert
        assertThat(result).isEqualTo("OK: %s".formatted(sequence));
        // given 2
        repository.addBloomFilter(bloomFilter, Integer.parseInt(sequence));
        // then 2
        result = repository.functionCall(produceSequence, keys, args);
        // assert 2
        assertThat(result).isEqualTo("EXISTS: %s".formatted(sequence));
        assertThat(jedis.rpop(queue.value())).isEqualTo(payload);
    }
}
