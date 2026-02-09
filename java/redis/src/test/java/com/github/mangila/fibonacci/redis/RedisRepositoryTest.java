package com.github.mangila.fibonacci.redis;

import com.github.mangila.fibonacci.redis.test.RedisTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest;
import org.springframework.context.annotation.Import;
import redis.clients.jedis.UnifiedJedis;

import static org.assertj.core.api.Assertions.assertThat;

@DataRedisTest
@RedisTestContainer
@Import({RedisRepository.class,
        RedisConfig.class})
class RedisRepositoryTest {

    @Autowired
    private UnifiedJedis jedis;

    @Autowired
    private RedisRepository repository;

    @Autowired
    private RedisKey bloomFilter;

    @Autowired
    private RedisKey zset;

    @Test
    void addZset() {
        int score = 1;
        String member = "member";
        var add = repository.addZset(zset, score, member);
        assertThat(add).isEqualTo(1L);
        var size = jedis.zcard(zset.value());
        assertThat(size).isEqualTo(1);
    }
}