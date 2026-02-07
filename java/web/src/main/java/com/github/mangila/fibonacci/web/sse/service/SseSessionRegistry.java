package com.github.mangila.fibonacci.web.sse.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.mangila.fibonacci.web.sse.model.SseSession;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

@Service
public class SseSessionRegistry {

    private static final Logger log = LoggerFactory.getLogger(SseSessionRegistry.class);
    private final Cache<String, CopyOnWriteArrayList<SseSession>> sessions = Caffeine.newBuilder()
            .maximumSize(100)
            .removalListener((String sessionId, CopyOnWriteArrayList<SseSession> sessions, RemovalCause cause) -> {
                if (cause.wasEvicted()) {
                    log.warn("Session evicted for {}", sessionId);
                    sessions.forEach(session -> session.emitter().complete());
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
        var emitter = session.emitter();
        emitter.onTimeout(() -> {
            log.info("SseSession timed out: {}", session);
            emitter.complete();
            remove(session);
        });
        emitter.onCompletion(() -> {
            log.info("SseSession completed: {}", session);
            remove(session);
        });
        emitter.onError(throwable -> {
            log.error("Error in SseSession emitter: {}", throwable.getMessage());
        });
    }

    @Nullable
    public CopyOnWriteArrayList<SseSession> getSessions(String channel) {
        return sessions.getIfPresent(channel);
    }

    public Stream<SseSession> getAllSessions() {
        return sessions.asMap()
                .values()
                .stream()
                .flatMap(List::stream);
    }

    private void remove(SseSession session) {
        final var channel = session.subscription().channel();
        sessions.asMap()
                .computeIfPresent(channel, (_, list) -> {
                    list.remove(session);
                    return list;
                });
    }
}