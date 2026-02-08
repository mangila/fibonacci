package com.github.mangila.fibonacci.jobrunr.job.zset.drain;

import com.github.mangila.fibonacci.redis.RedisRepository;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;

public class DrainZsetBootstrap {

    private static final Logger log = LoggerFactory.getLogger(DrainZsetBootstrap.class);

    private final RedisRepository redisRepository;
    private final Resource drainZsetScript;

    public DrainZsetBootstrap(RedisRepository redisRepository, Resource drainZsetScript) {
        this.redisRepository = redisRepository;
        this.drainZsetScript = drainZsetScript;
    }

    @EventListener(ApplicationReadyEvent.class)
    void init() {
        try {
            @Language("Lua")
            String code = drainZsetScript.getContentAsString(StandardCharsets.UTF_8);
            redisRepository.functionLoad(code);
        } catch (Exception e) {
            log.error("Err loading function: {}", drainZsetScript.getFilename(), e);
        }
    }
}
