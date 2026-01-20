package com.github.mangila.fibonacci.sse;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.mangila.ensure4j.Ensure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class SseEmitterRegistry implements SmartLifecycle, BeanNameAware {

    private static final Logger log = LoggerFactory.getLogger(SseEmitterRegistry.class);

    private final Cache<String, CopyOnWriteArraySet<SseSession>> sseSessionCache;
    private String beanName;
    private volatile boolean running = false;

    public SseEmitterRegistry(Cache<String, CopyOnWriteArraySet<SseSession>> sseSessionCache) {
        this.sseSessionCache = sseSessionCache;
    }

    public SseSession subscribe(String sessionId, String streamKey) {
        CopyOnWriteArraySet<SseSession> sessions = add(sessionId, streamKey);
        SseSession currentSession = sessions.stream().filter(session -> session.streamKey().equals(streamKey))
                .findFirst()
                .orElseThrow();
        SseEmitter emitter = currentSession.emitter();
        emitter.onError((ex) -> {
            log.error("SSE Error for user {}: {}", sessionId, streamKey);
        });

        emitter.onTimeout(() -> {
            log.warn("SSE Timeout for user {}: {}", sessionId, streamKey);
        });

        emitter.onCompletion(() -> {
            log.info("SSE Completed for user {}: {}", sessionId, streamKey);
            remove(sessionId, streamKey);
        });
        return currentSession;
    }

    public void remove(String sessionId, String streamKey) {
        var set = sseSessionCache.getIfPresent(sessionId);
        if (set != null) {
            set.removeIf(session -> {
                if (session.streamKey().equals(streamKey)) {
                    session.complete();
                    return true;
                }
                return false;
            });
        }
    }

    public void removeWithError(String sessionId, String streamKey, Throwable throwable) {
        var set = sseSessionCache.getIfPresent(sessionId);
        if (set != null) {
            set.removeIf(session -> {
                if (session.streamKey().equals(streamKey)) {
                    session.completeWithError(throwable);
                    return true;
                }
                return false;
            });
        }
    }

    public void remove(String sessionId) {
        sseSessionCache.invalidate(sessionId);
    }

    public ConcurrentMap<String, CopyOnWriteArraySet<SseSession>> asMap() {
        return sseSessionCache.asMap();
    }


    public CopyOnWriteArraySet<SseSession> add(String id, String streamKey) {
        return asMap()
                .compute(id, (_, existingSet) -> {
                    var set = existingSet == null ? new CopyOnWriteArraySet<SseSession>() : existingSet;
                    // with an HTTP2 connection, we can bump this number
                    Ensure.max(6, set.size(), "Too many SSE sessions for %s".formatted(id));
                    var emitter = new SseEmitter(Duration.ofMinutes(60).toMillis());
                    var session = new SseSession(id, streamKey, emitter);
                    set.add(session);
                    return set;
                });
    }

    public void clear() {
        asMap().forEach((_, sessions) -> sessions.forEach(SseSession::complete));
        sseSessionCache.invalidateAll();
    }

    public long size() {
        return sseSessionCache.estimatedSize();
    }

    @Override
    public void start() {
        log.info("Starting bean {}: {}", beanName, this);
        running = true;
    }

    @Override
    public void stop() {
        log.info("Stopping bean {}: {}", beanName, this);
        running = false;
        clear();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
}
