package com.github.mangila.fibonacci.web.sse;

import com.github.mangila.fibonacci.shared.properties.SseProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Predicate;

@Service
public class SseScheduler {

    private static final Logger log = LoggerFactory.getLogger(SseScheduler.class);

    private final SseProperties sseProperties;
    private final SimpleAsyncTaskScheduler sseTaskScheduler;
    private final SseEmitterRegistry sseEmitterRegistry;

    public SseScheduler(SseProperties sseProperties,
                        SimpleAsyncTaskScheduler sseTaskScheduler,
                        SseEmitterRegistry sseEmitterRegistry) {
        this.sseProperties = sseProperties;
        this.sseTaskScheduler = sseTaskScheduler;
        this.sseEmitterRegistry = sseEmitterRegistry;
    }

    @EventListener(ApplicationReadyEvent.class)
    void init() {
        if (sseProperties.getHeartbeat().isEnabled()) {
            sseTaskScheduler.scheduleWithFixedDelay(this::heartbeat, sseProperties.getHeartbeat().getInterval());
        }
        sseTaskScheduler.scheduleWithFixedDelay(this::cleanupChannels, sseProperties.getCleanupPeriod());
    }

    public void heartbeat() {
        sseEmitterRegistry.getAllSession()
                .forEach(sseSession -> {
                    try {
                        log.info("Sending heartbeat to session for channel: {} - {}", sseSession.channel(), sseSession.streamKey());
                        sseSession.sendHeartbeat();
                    } catch (IOException e) {
                        sseSession.completeWithError(e);
                    }
                });
    }

    public void cleanupChannels() {
        sseEmitterRegistry.getKeys()
                .removeIf(Predicate.not(sseEmitterRegistry::hasSessions));
    }
}
