package com.github.mangila.fibonacci.redis;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;

@Configuration
public class RedisBootstrap {

    private static final Logger log = LoggerFactory.getLogger(RedisBootstrap.class);

    private final Environment environment;
    private final RedisRepository redisRepository;
    private final RedisKey bloomFilter;
    private final Resource drainZsetScript;
    private final Resource produceSequenceScript;

    public RedisBootstrap(
            Environment environment,
            RedisRepository redisRepository,
            RedisKey bloomFilter,
            @Value("classpath:/functions/drain_zset.lua") Resource drainZsetScript,
            @Value("classpath:/functions/produce_sequence.lua") Resource produceSequenceScript) {
        this.environment = environment;
        this.redisRepository = redisRepository;
        this.bloomFilter = bloomFilter;
        this.drainZsetScript = drainZsetScript;
        this.produceSequenceScript = produceSequenceScript;
    }

    @PostConstruct
    void init() {
        if (environment.matchesProfiles("dev")) {
            log.warn("Flushing Redis database");
            redisRepository.flushEverything();
        }
        log.info("Redis bootstrap started");
        tryInitBloomFilter();
        tryLoadFunctions();
        log.info("Redis bootstrap completed");
    }

    private void tryInitBloomFilter() {
        try {
            log.info("Creating bloom filter: {}", bloomFilter);
            var ok = redisRepository.createBloomFilter(bloomFilter, 0.001, 100_000);
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
            log.info("Loading Lua function: {}", produceSequenceScript.getFilename());
            code = produceSequenceScript.getContentAsString(StandardCharsets.UTF_8);
            var ok = redisRepository.functionLoad(code);
            log.info("Function loaded: {}", ok);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}
