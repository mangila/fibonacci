package com.github.mangila.fibonacci.jobrunr.job.consumer;

import com.github.mangila.fibonacci.redis.RedisBootstrap;
import com.github.mangila.fibonacci.redis.RedisKey;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

public class ConsumerBootstrap {

    private final RedisKey bloomFilter;
    private final RedisBootstrap redisBootstrap;

    public ConsumerBootstrap(RedisKey bloomFilter,
                             RedisBootstrap redisBootstrap
    ) {
        this.bloomFilter = bloomFilter;
        this.redisBootstrap = redisBootstrap;
    }

    @EventListener(ApplicationReadyEvent.class)
    void initRedis() {
        redisBootstrap.tryInitBloomFilter(bloomFilter);
    }
}
