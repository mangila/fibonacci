package com.github.mangila.fibonacci.web.sse.service;

import com.github.mangila.fibonacci.web.sse.model.Session;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static java.util.concurrent.TimeUnit.SECONDS;

@Service
public class SseScheduler {

    private final SseSessionRegistry sessionRegistry;

    public SseScheduler(SseSessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Scheduled(
            scheduler = "sseTaskScheduler",
            fixedRate = 10,
            timeUnit = SECONDS
    )
    void heartbeat() {
        sessionRegistry.getAllSessions()
                .forEach(Session::sendHeartbeat);
    }
}
