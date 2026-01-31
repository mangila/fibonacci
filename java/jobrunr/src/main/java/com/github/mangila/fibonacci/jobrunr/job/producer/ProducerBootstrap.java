package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.redis.RedisBootstrap;
import com.github.mangila.fibonacci.redis.RedisKey;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;

public class ProducerBootstrap {

    private final RedisKey bloomFilter;
    private final RedisBootstrap redisBootstrap;
    private final Resource produceSequenceScript;

    public ProducerBootstrap(RedisKey bloomFilter,
                             RedisBootstrap redisBootstrap,
                             Resource produceSequenceScript
    ) {
        this.bloomFilter = bloomFilter;
        this.redisBootstrap = redisBootstrap;
        this.produceSequenceScript = produceSequenceScript;
    }

    @EventListener(ApplicationReadyEvent.class)
    void initRedis() {
        redisBootstrap.tryInitBloomFilter(bloomFilter);
        redisBootstrap.tryLoadFunction(produceSequenceScript);
    }
}
