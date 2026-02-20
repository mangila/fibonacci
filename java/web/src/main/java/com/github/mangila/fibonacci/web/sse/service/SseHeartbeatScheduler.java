package com.github.mangila.fibonacci.web.sse.service;

import com.github.mangila.fibonacci.web.sse.model.SseSession;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static java.util.concurrent.TimeUnit.SECONDS;

@Service
public class SseHeartbeatScheduler {

    private final SseSessionRegistry sessionRegistry;

    public SseHeartbeatScheduler(SseSessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Scheduled(
            scheduler = "sseTaskScheduler",
            fixedRate = 10,
            timeUnit = SECONDS
    )
    void sendHeartbeats() {
        sessionRegistry.getAllSessions()
                .forEach(SseSession::sendHeartbeat);
    }
}
