package com.github.mangila.fibonacci.web.sse.service;

import com.github.mangila.fibonacci.web.sse.model.SseSession;
import com.github.mangila.fibonacci.web.sse.properties.SseProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

import java.util.Map;

public class SseScheduler {

    private static final Logger log = LoggerFactory.getLogger(SseScheduler.class);

    private final SseProperties sseProperties;
    private final SimpleAsyncTaskScheduler sseTaskScheduler;
    private final SseSessionRegistry sessionRegistry;

    public SseScheduler(SseProperties sseProperties,
                        SimpleAsyncTaskScheduler sseTaskScheduler,
                        SseSessionRegistry sessionRegistry) {
        this.sseProperties = sseProperties;
        this.sseTaskScheduler = sseTaskScheduler;
        this.sessionRegistry = sessionRegistry;
    }

    @EventListener(ApplicationReadyEvent.class)
    void init() {
        final var cleanupPeriod = sseProperties.getCleanupPeriod();
        log.info("Scheduled cleanup every {}", cleanupPeriod);
        sseTaskScheduler.scheduleWithFixedDelay(() -> {
            sessionRegistry.getAllEntries()
                    .stream()
                    .filter(entry -> entry.getValue().isEmpty())
                    .map(Map.Entry::getKey)
                    .peek(channel -> log.info("Cleaning up idle channel: {}", channel))
                    .forEach(sessionRegistry::removeChannel);
        }, cleanupPeriod);
        final var heartbeatInterval = sseProperties.getHeartbeat()
                .getInterval();
        log.info("Scheduled heartbeat every {}", heartbeatInterval);
        sseTaskScheduler.scheduleWithFixedDelay(() -> {
            sessionRegistry.getAllSessions()
                    .forEach(SseSession::sendHeartbeat);
        }, heartbeatInterval);
    }
}
