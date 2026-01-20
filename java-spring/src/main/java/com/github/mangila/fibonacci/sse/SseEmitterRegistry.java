package com.github.mangila.fibonacci.sse;

import io.github.mangila.ensure4j.Ensure;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.stream.Stream;

public class SseEmitterRegistry implements SmartLifecycle, BeanNameAware {

    private static final Logger log = LoggerFactory.getLogger(SseEmitterRegistry.class);

    private final SseSessionCache cache;
    private String beanName;
    private volatile boolean open = false;

    public SseEmitterRegistry(SseSessionCache cache) {
        this.cache = cache;
    }

    public SseSession subscribe(String channel, String streamKey) {
        Ensure.isTrue(open, "Registry %s is not open".formatted(beanName));
        cache.tryAdd(channel, streamKey);
        SseSession session = cache.getSession(channel, streamKey);
        Ensure.notNull(session, "Session not found for %s:%s".formatted(channel, streamKey));
        // noinspection ConstantConditions
        SseEmitter emitter = session.emitter();
        emitter.onError((ex) -> log.warn("SSE Error for user {}: {}", channel, streamKey, ex));
        emitter.onTimeout(() -> log.warn("SSE Timeout for user {}: {}", channel, streamKey));
        emitter.onCompletion(() -> {
            log.info("SSE Completed for user {}: {}", channel, streamKey);
            cache.removeSession(channel, streamKey);
            if (!cache.hasSessions(channel)) {
                cache.invalidate(channel);
            }
        });
        return session;
    }

    public Stream<SseSession> getAllSession() {
        return cache.getAllSession();
    }

    @NonNull
    public SseSession getSession(String channel, String streamKey) {
        SseSession session = cache.getSession(channel, streamKey);
        Ensure.notNull(session, "Session not found for %s:%s".formatted(channel, streamKey));
        // noinspection ConstantConditions
        return session;
    }

    @Override
    public void start() {
        log.info("Starting registry {}: {}", beanName, this);
        open = true;
    }

    @Override
    public void stop() {
        log.info("Stopping registry {}: {}", beanName, this);
        open = false;
        cache.getAllSession().forEach(SseSession::complete);
    }

    @Override
    public boolean isRunning() {
        return open;
    }

    @Override
    public void setBeanName(@NonNull String name) {
        Ensure.notNull(name, "Bean name cannot be null");
        this.beanName = name;
    }
}
