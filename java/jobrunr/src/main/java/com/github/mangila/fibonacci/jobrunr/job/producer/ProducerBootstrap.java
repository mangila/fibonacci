package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.redis.RedisConfig;
import com.github.mangila.fibonacci.redis.RedisKey;
import com.github.mangila.fibonacci.redis.RedisRepository;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;

public class ProducerBootstrap {

    private static final Logger log = LoggerFactory.getLogger(ProducerBootstrap.class);

    private final RedisRepository redisRepository;
    private final RedisKey bloomFilter;
    private final Resource produceSequenceScript;

    public ProducerBootstrap(RedisRepository redisRepository,
                             RedisKey bloomFilter,
                             Resource produceSequenceScript
    ) {
        this.redisRepository = redisRepository;
        this.bloomFilter = bloomFilter;
        this.produceSequenceScript = produceSequenceScript;
    }

    @EventListener(ApplicationReadyEvent.class)
    void init() {
        try {
            redisRepository.createBloomFilter(bloomFilter,
                    RedisConfig.DEFAULT_BLOOM_FILTER_ERROR_RATE,
                    RedisConfig.DEFAULT_BLOOM_FILTER_CAPACITY);
        } catch (Exception e) {
            log.error("Err creating bloom filter", e);
        }
        try {
            @Language("Lua")
            String code = produceSequenceScript.getContentAsString(StandardCharsets.UTF_8);
            redisRepository.functionLoad(code);
        } catch (Exception e) {
            log.error("Err loading function: {}", produceSequenceScript.getFilename(), e);
        }
    }
}
