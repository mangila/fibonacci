package com.github.mangila.fibonacci.redis;

import com.github.mangila.fibonacci.redis.test.RedisTestContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest;
import org.springframework.context.annotation.Import;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataRedisTest
@RedisTestContainer
@Import({RedisRepository.class,
        RedisConfig.class,
        RedisBootstrap.class})
class RedisRepositoryTest {

    @Autowired
    private UnifiedJedis jedis;

    @Autowired
    private RedisRepository repository;

    @Autowired
    private FunctionName drainZset;

    @Autowired
    private FunctionName addZset;

    @Autowired
    private RedisKey zsetKey;

    @Autowired
    private RedisKey bloomFilterKey;

    @Autowired
    private RedisKey queueKey;

    @Autowired
    private RedisKey valueKey;

    @Autowired
    private RedisKey streamKey;

    @AfterEach
    void tearDown() {
        jedis.flushAll();
    }

    @Test
    @DisplayName("Tests idempotent addition to Zset and check Bloom filter")
    void doubleWriteAddZset() {
        String score = "1";
        String member = "member";
        assertThat(repository.existsInBloomFilter(bloomFilterKey, score)).isFalse();
        Object ok = repository.functionCall(
                addZset,
                List.of(zsetKey, bloomFilterKey),
                List.of(score, member)
        );
        assertThat(ok).isEqualTo("OK");
        assertThat(repository.existsInBloomFilter(bloomFilterKey, score)).isTrue();
        ok = repository.functionCall(
                addZset,
                List.of(zsetKey, bloomFilterKey),
                List.of(score, member));
        assertThat(ok).isEqualTo("OK");
        assertThat(repository.existsInBloomFilter(bloomFilterKey, score)).isTrue();
        long size = jedis.zcard(zsetKey.value());
        assertThat(size).isEqualTo(1);
    }

    @Test
    @DisplayName("Confirms ZSET_EMPTY error when draining empty Zset")
    void drainZsetFailZsetEmpty() {
        assertThatThrownBy(() -> repository.functionCall(
                drainZset,
                List.of(zsetKey, streamKey, valueKey),
                Collections.emptyList()))
                .isInstanceOf(JedisDataException.class)
                .hasMessageContaining("ZSET_EMPTY");
    }

    @Test
    @DisplayName("Confirms SEQUENCE_MISMATCH error when draining Zset with different sequence value")
    void drainZsetFailSequenceMismatch() {
        String score = "2";
        String member = "member";
        var fnOk = repository.functionCall(
                addZset,
                List.of(zsetKey, bloomFilterKey),
                List.of(score, member)
        );
        assertThat(fnOk).isEqualTo("OK");
        assertThat(repository.existsInBloomFilter(bloomFilterKey, score)).isTrue();
        assertThatThrownBy(() -> repository.functionCall(
                drainZset,
                List.of(zsetKey, streamKey, valueKey),
                Collections.emptyList()))
                .isInstanceOf(JedisDataException.class)
                .hasMessageContaining("SEQUENCE_MISMATCH");
    }

    @Test
    @DisplayName("Drains Zset and increments VALUE")
    void drainZset() {
        String score = "1";
        String member = "member";
        var fnOk = repository.functionCall(
                addZset,
                List.of(zsetKey, bloomFilterKey),
                List.of(score, member)
        );
        assertThat(fnOk).isEqualTo("OK");
        fnOk = repository.functionCall(
                drainZset,
                List.of(zsetKey, streamKey, valueKey),
                Collections.emptyList());
        assertThat(repository.existsInBloomFilter(bloomFilterKey, score)).isTrue();
        assertThat(fnOk).isEqualTo("1");
        var value = jedis.get(valueKey.value());
        assertThat(value).isEqualTo("2");
        var size = jedis.zcard(zsetKey.value());
        assertThat(size).isEqualTo(0);
        long streamSize = jedis.xlen(streamKey.value());
        assertThat(streamSize).isEqualTo(1);
    }

    @Test
    @DisplayName("Pushes to Redis list")
    void pushList() {
        repository.pushList(queueKey, "1");
        repository.pushList(queueKey, "2");
        var l = jedis.blpop(10, queueKey.value());
        assertThat(l).isNotNull();
        assertThat(l.get(0)).isEqualTo(queueKey.value());
        assertThat(l.get(1)).isEqualTo("2");
    }
}