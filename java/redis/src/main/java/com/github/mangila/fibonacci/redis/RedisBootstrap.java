package com.github.mangila.fibonacci.redis;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;

@Configuration
public class RedisBootstrap {

    private static final Logger log = LoggerFactory.getLogger(RedisBootstrap.class);

    private final Environment environment;
    private final RedisRepository redisRepository;

    public RedisBootstrap(
            Environment environment,
            RedisRepository redisRepository) {
        this.environment = environment;
        this.redisRepository = redisRepository;
    }

    @PostConstruct
    void init() {
        if (environment.matchesProfiles("dev")) {
            log.warn("Flushing Redis database");
            redisRepository.flushEverything();
        }
    }

    public void tryInitBloomFilter(RedisKey bloomFilter) {
        try {
            log.info("Creating bloom filter: {}", bloomFilter);
            var ok = redisRepository.createBloomFilter(bloomFilter, 0.001, 100_000);
            log.info("Bloom filter created: {}", ok);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public void tryLoadFunction(Resource resource) {
        try {
            log.info("Loading Lua function: {}", resource.getFilename());
            String code = resource.getContentAsString(StandardCharsets.UTF_8);
            var ok = redisRepository.functionLoad(code);
            log.info("Function loaded: {}", ok);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}
