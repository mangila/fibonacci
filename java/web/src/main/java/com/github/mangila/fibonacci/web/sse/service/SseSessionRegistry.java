package com.github.mangila.fibonacci.web.sse.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.mangila.fibonacci.web.sse.model.SseSession;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseSessionRegistry {

    private final Cache<String, CopyOnWriteArrayList<SseSession>> sessions = Caffeine.newBuilder()
            .maximumSize(100)
            .evictionListener((key, _, cause) -> {
                if (cause.wasEvicted()) {
                    System.out.println("Evicted: " + key);
                }
            })
            .build();

    public void add(SseSession session) {
        final var channel = session.subscription().channel();
        sessions.asMap()
                .compute(channel, (_, list) -> {
                    if (list == null) {
                        list = new CopyOnWriteArrayList<>();
                    }
                    return list;
                }).add(session);
    }

    @Nullable
    public CopyOnWriteArrayList<SseSession> getSessions(String channel) {
        return sessions.getIfPresent(channel);
    }
}