package com.github.mangila.fibonacci.sse;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.mangila.ensure4j.Ensure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class SseEmitterRegistry {

    private static final Logger log = LoggerFactory.getLogger(SseEmitterRegistry.class);

    private final Cache<String, SseSession> sseSessionCache;

    public SseEmitterRegistry(Cache<String, SseSession> sseSessionCache) {
        this.sseSessionCache = sseSessionCache;
    }

    public void add(String id, SseSession emitter) {
        sseSessionCache.put(id, emitter);
    }

    public void remove(String id) {
        SseSession session = getOrThrow(id);
        session.complete();
        sseSessionCache.invalidate(id);
    }

    public void removeWithError(String id, Throwable throwable) {
        SseSession session = getOrThrow(id);
        session.completeWithError(throwable);
        sseSessionCache.invalidate(id);
    }

    public SseSession getOrThrow(String id) {
        return Ensure.notNullOrElseThrow(sseSessionCache.getIfPresent(id),
                () -> new IllegalArgumentException("SSE session not found for id: " + id));
    }

    public ConcurrentMap<String, SseSession> asMap() {
        return sseSessionCache.asMap();
    }

    public SseEmitter subscribe(String username) {
        var emitter = new SseEmitter(Duration.ofMinutes(60).toMillis());
        var session = new SseSession(username, new AtomicBoolean(false), emitter);
        add(username, session);
        emitter.onError((ex) -> {
            log.error("SSE Error for user {}: {}", username, ex.getMessage());
            remove(username);
        });

        emitter.onTimeout(() -> {
            log.warn("SSE Timeout for user {}", username);
            remove(username);
        });

        emitter.onCompletion(() -> {
            log.info("SSE Completed for user {}", username);
            remove(username);
        });
        return emitter;
    }

    public void subscribeLivestream(String username) {
        SseSession session = getOrThrow(username);
        session.setLivestream(true);
    }

    public void unsubscribe(String username) {
        remove(username);
    }

    public void unsubscribeLivestream(String username) {
        var session = getOrThrow(username);
        session.setLivestream(false);
    }
}
