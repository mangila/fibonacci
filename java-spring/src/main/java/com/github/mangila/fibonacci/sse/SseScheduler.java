package com.github.mangila.fibonacci.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Service
public class SseScheduler {

    private static final Logger log = LoggerFactory.getLogger(SseScheduler.class);

    private final SseEmitterRegistry sseEmitterRegistry;

    public SseScheduler(SseEmitterRegistry sseEmitterRegistry) {
        this.sseEmitterRegistry = sseEmitterRegistry;
    }

    @Scheduled(fixedRate = 10,
            timeUnit = TimeUnit.SECONDS,
            scheduler = "sseTaskScheduler")
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

    @Scheduled(fixedRate = 10,
            timeUnit = TimeUnit.MINUTES,
            scheduler = "sseTaskScheduler")
    public void cleanupChannels() {
        sseEmitterRegistry.getKeys()
                .removeIf(Predicate.not(sseEmitterRegistry::hasSessions));
    }
}
