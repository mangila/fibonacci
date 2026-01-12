package com.github.mangila.fibonacci.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.mangila.fibonacci.sse.SseSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SseConfig {

    private static final Logger log = LoggerFactory.getLogger(SseConfig.class);

    @Bean
    Cache<String, SseSession> sseSessionCache() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .removalListener((String username, SseSession session, RemovalCause cause) -> {
                    if (cause.wasEvicted()) {
                        log.warn("Session evicted for {}", username);
                        session.completeWithError(new RuntimeException("Session evicted"));
                    }
                })
                .build();
    }
}
