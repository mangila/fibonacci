package com.github.mangila.fibonacci.web.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.mangila.fibonacci.web.sse.SseEmitterRegistry;
import com.github.mangila.fibonacci.web.sse.model.SseSession;
import com.github.mangila.fibonacci.web.sse.SseSessionCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CopyOnWriteArraySet;

@Configuration
public class SseConfig {

    private static final Logger log = LoggerFactory.getLogger(SseConfig.class);

    Cache<String, CopyOnWriteArraySet<SseSession>> sseSessionCache() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .removalListener((String sessionId, CopyOnWriteArraySet<SseSession> sessions, RemovalCause cause) -> {
                    if (cause.wasEvicted()) {
                        log.warn("Session evicted for {}", sessionId);
                        sessions.forEach(sseSession -> sseSession.completeWithError(new RuntimeException("Session evicted")));
                    }
                })
                .build();
    }

    @Bean
    SseEmitterRegistry sseEmitterRegistry() {
        return new SseEmitterRegistry(new SseSessionCache(sseSessionCache()));
    }
}
