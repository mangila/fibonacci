package com.github.mangila.fibonacci.sse;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.EnsureException;
import org.jspecify.annotations.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

public class SseSessionCache {

    private final Cache<String, CopyOnWriteArraySet<SseSession>> cache;

    public SseSessionCache(Cache<String, CopyOnWriteArraySet<SseSession>> cache) {
        this.cache = cache;
    }

    @Nullable
    public SseSession getSession(String channel, String streamKey) {
        var set = cache.getIfPresent(channel);
        if (set != null) {
            return set.stream()
                    .filter(session -> session.streamKey().equals(streamKey))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public void tryAdd(String channel, String streamKey) throws EnsureException {
        cache.asMap()
                .compute(channel, (_, existingSet) -> {
                    var set = existingSet == null ? new CopyOnWriteArraySet<SseSession>() : existingSet;
                    // with an HTTP2 connection, we can bump this number
                    Ensure.max(6, set.size(), "Too many SSE sessions for %s".formatted(channel));
                    var emitter = new SseEmitter(Duration.ofMinutes(60).toMillis());
                    var session = new SseSession(channel, streamKey, emitter);
                    set.add(session);
                    return set;
                });
    }

    public void removeSession(String channel, String streamKey) {
        cache.asMap().computeIfPresent(channel, (_, sessions) -> {
            sessions.removeIf(session -> session.streamKey().equals(streamKey));
            return sessions;
        });
    }

    public Stream<SseSession> getAllSession() {
        return cache.asMap()
                .values()
                .stream()
                .flatMap(CopyOnWriteArraySet::stream);
    }

    public boolean hasSessions(String channel) {
        var set = cache.getIfPresent(channel);
        if (set != null) {
            return set.isEmpty();
        }
        return false;
    }

    public void invalidate(String channel) {
        cache.invalidate(channel);
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }
}
