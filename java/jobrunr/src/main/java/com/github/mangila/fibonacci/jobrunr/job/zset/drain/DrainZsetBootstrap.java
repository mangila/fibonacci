package com.github.mangila.fibonacci.jobrunr.job.zset.drain;

import com.github.mangila.fibonacci.redis.RedisBootstrap;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;

public class DrainZsetBootstrap {

    private final RedisBootstrap redisBootstrap;
    private final Resource drainZsetScript;

    public DrainZsetBootstrap(RedisBootstrap redisBootstrap, Resource drainZsetScript) {
        this.redisBootstrap = redisBootstrap;
        this.drainZsetScript = drainZsetScript;
    }

    @EventListener(ApplicationReadyEvent.class)
    void init() {
        redisBootstrap.tryLoadFunction(drainZsetScript);
    }
}
