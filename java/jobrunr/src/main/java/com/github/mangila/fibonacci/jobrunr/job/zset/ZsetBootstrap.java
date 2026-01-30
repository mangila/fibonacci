package com.github.mangila.fibonacci.jobrunr.job.zset;

import com.github.mangila.fibonacci.redis.RedisBootstrap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(prefix = "app.produce", name = "enabled", havingValue = "true")
@Service
public class ZsetBootstrap {

    private final RedisBootstrap redisBootstrap;
    private final Resource drainZsetScript;

    public ZsetBootstrap(RedisBootstrap redisBootstrap,
                         @Value("classpath:/functions/drain_zset.lua") Resource drainZsetScript
    ) {
        this.redisBootstrap = redisBootstrap;
        this.drainZsetScript = drainZsetScript;
    }

    @EventListener(ApplicationReadyEvent.class)
    void initRedis() {
        redisBootstrap.tryLoadFunction(drainZsetScript);
    }
}
