package com.github.mangila.fibonacci.web.sse.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.mangila.fibonacci.web.sse.model.SseSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class SseSessionRegistry {

    private static final Logger log = LoggerFactory.getLogger(SseSessionRegistry.class);

    private final Cache<String, CopyOnWriteArrayList<SseSession>> sessions = Caffeine.newBuilder()
            .maximumSize(100)
            .removalListener((String channel, CopyOnWriteArrayList<SseSession> sseSessions, RemovalCause cause) -> {
                if (cause.wasEvicted()) {
                    log.warn("Session evicted for {}", channel);
                    sseSessions.forEach(sseSession -> sseSession.emitter().complete());
                }
            })
            .build();

    public void add(SseSession sseSession) {
        final var channel = sseSession.sseSubscription().channel();
        sessions.asMap()
                .compute(channel, (_, list) -> {
                    if (list == null) {
                        list = new CopyOnWriteArrayList<>();
                    }
                    return list;
                }).add(sseSession);
        var emitter = sseSession.emitter();
        emitter.onTimeout(() -> {
            log.info("SseSession timed out: {}", sseSession);
            emitter.complete();
            removeSession(sseSession);
        });
        emitter.onCompletion(() -> {
            log.info("SseSession completed: {}", sseSession);
            removeSession(sseSession);
        });
        emitter.onError(throwable -> {
            log.error("Error in SseSession emitter: {}", throwable.getMessage());
        });
    }

    public void removeChannel(String channel) {
        sessions.invalidate(channel);
    }

    public Set<Map.Entry<String, CopyOnWriteArrayList<SseSession>>> getAllEntries() {
        return sessions.asMap().entrySet();
    }

    public Stream<SseSession> getAllSessions() {
        return sessions.asMap()
                .values()
                .stream()
                .flatMap(List::stream);
    }

    private void removeSession(SseSession sseSession) {
        final var channel = sseSession.sseSubscription().channel();
        sessions.asMap()
                .computeIfPresent(channel, (_, list) -> {
                    list.remove(sseSession);
                    return list;
                });
    }
}