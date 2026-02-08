package com.github.mangila.fibonacci.jobrunr.job.consumer;

import com.github.mangila.fibonacci.redis.RedisConfig;
import com.github.mangila.fibonacci.redis.RedisKey;
import com.github.mangila.fibonacci.redis.RedisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

public class ConsumerBootstrap {

    private static final Logger log = LoggerFactory.getLogger(ConsumerBootstrap.class);
    private final RedisKey bloomFilter;
    private final RedisRepository redisRepository;

    public ConsumerBootstrap(RedisKey bloomFilter,
                             RedisRepository redisRepository
    ) {
        this.bloomFilter = bloomFilter;
        this.redisRepository = redisRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    void initRedis() {
        try {
            redisRepository.createBloomFilter(bloomFilter,
                    RedisConfig.DEFAULT_BLOOM_FILTER_ERROR_RATE,
                    RedisConfig.DEFAULT_BLOOM_FILTER_CAPACITY);
        } catch (Exception e) {
            log.error("Err creating bloom filter", e);
        }
    }
}
