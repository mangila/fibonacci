package com.github.mangila.fibonacci.redis;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;

@Configuration
public class RedisBootstrap {

    private static final Logger log = LoggerFactory.getLogger(RedisBootstrap.class);

    private final RedisRepository redisRepository;
    private final RedisKey bloomFilterKey;
    private final Resource addZsetScript;
    private final Resource drainZsetScript;

    public RedisBootstrap(
            RedisRepository redisRepository,
            RedisKey bloomFilterKey,
            @Value("classpath:/functions/drain_zset.lua") Resource drainZsetScript,
            @Value("classpath:/functions/add_zset.lua") Resource addZsetScript) {
        this.redisRepository = redisRepository;
        this.bloomFilterKey = bloomFilterKey;
        this.addZsetScript = addZsetScript;
        this.drainZsetScript = drainZsetScript;
    }

    @PostConstruct
    void init() {
        log.info("Redis bootstrap started");
        tryInitBloomFilter();
        tryLoadFunctions();
        log.info("Redis bootstrap completed");
    }

    private void tryInitBloomFilter() {
        try {
            log.info("Creating bloom filter: {}", bloomFilterKey);
            var ok = redisRepository.createBloomFilter(bloomFilterKey, 0.001, 100_000);
            log.info("Bloom filter created: {}", ok);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    private void tryLoadFunctions() {
        String code = null;
        try {
            log.info("Loading Lua function: {}", drainZsetScript.getFilename());
            code = drainZsetScript.getContentAsString(StandardCharsets.UTF_8);
            var ok = redisRepository.functionLoad(code);
            log.info("Function loaded: {}", ok);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        try {
            log.info("Loading Lua function: {}", addZsetScript.getFilename());
            code = addZsetScript.getContentAsString(StandardCharsets.UTF_8);
            var ok = redisRepository.functionLoad(code);
            log.info("Function loaded: {}", ok);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}
