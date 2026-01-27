package com.github.mangila.fibonacci.web.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.mangila.fibonacci.web.sse.SseEmitterRegistry;
import com.github.mangila.fibonacci.web.sse.SseSessionCache;
import com.github.mangila.fibonacci.web.sse.model.SseChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SseConfig {

    private static final Logger log = LoggerFactory.getLogger(SseConfig.class);

    Cache<String, SseChannel> sseSessionCache() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .removalListener((String channel, SseChannel sseChannel, RemovalCause cause) -> {
                    if (cause.wasEvicted()) {
                        log.warn("Channel {} evicted", channel);
                        sseChannel.completeAndClear();
                    }
                })
                .build();
    }

    @Bean
    SseEmitterRegistry sseEmitterRegistry() {
        return new SseEmitterRegistry(new SseSessionCache(sseSessionCache()));
    }
}
