package com.github.mangila.fibonacci.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class SseHeartbeatScheduler {

    private static final Logger log = LoggerFactory.getLogger(SseHeartbeatScheduler.class);

    private final SseEmitterRegistry queryEmitterRegistry;
    private final SseEmitterRegistry livestreamEmitterRegistry;

    public SseHeartbeatScheduler(@Qualifier("queryEmitterRegistry") SseEmitterRegistry queryEmitterRegistry,
                                 @Qualifier("livestreamEmitterRegistry") SseEmitterRegistry livestreamEmitterRegistry) {
        this.queryEmitterRegistry = queryEmitterRegistry;
        this.livestreamEmitterRegistry = livestreamEmitterRegistry;
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void heartbeatLivestream() {
        log.info("Sending heartbeat to livestream sessions");
        livestreamEmitterRegistry.asMap().forEach((sessionId, sessions) -> {
            sessions.removeIf(session -> {
                try {
                    log.info("Sending heartbeat to livestream session {}: {}", sessionId, session.streamKey());
                    session.sendHeartbeat();
                    return false;
                } catch (IOException e) {
                    log.error("Error sending heartbeat to livestream session {}: {}", sessionId, session.streamKey());
                    session.completeWithError(e);
                    return true;
                }
            });
            if (sessions.isEmpty()) {
                livestreamEmitterRegistry.remove(sessionId);
            }
        });
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void heartbeatQuery() {
        log.info("Sending heartbeat to query sessions");
        queryEmitterRegistry.asMap().forEach((sessionId, sessions) -> {
            sessions.removeIf(session -> {
                try {
                    log.info("Sending heartbeat to query session {}: {}", sessionId, session.streamKey());
                    session.sendHeartbeat();
                    return false;
                } catch (IOException e) {
                    log.error("Error sending heartbeat to query session {}: {}", sessionId, session.streamKey());
                    session.completeWithError(e);
                    return true;
                }
            });
        });
    }
}
