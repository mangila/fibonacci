package com.github.mangila.fibonacci.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.mangila.fibonacci.sse.SseSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SseConfig {

    @Bean
    Cache<String, SseSession> sseSessionCache() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .build();
    }
}
