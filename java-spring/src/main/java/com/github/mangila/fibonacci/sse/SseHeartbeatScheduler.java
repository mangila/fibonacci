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

    @Scheduled(fixedRate = 5,
            timeUnit = TimeUnit.SECONDS,
            scheduler = "heartbeatScheduler")
    public void heartbeatLivestream() {
        log.info("Sending heartbeat to livestream sessions");
        livestreamEmitterRegistry.getAllSession()
                .forEach(sseSession -> {
                    try {
                        sseSession.sendHeartbeat();
                    } catch (IOException e) {
                        sseSession.completeWithError(e);
                    }
                });
    }

    @Scheduled(fixedRate = 5,
            timeUnit = TimeUnit.SECONDS,
            scheduler = "heartbeatScheduler")
    public void heartbeatQuery() {
        log.info("Sending heartbeat to query sessions");
        queryEmitterRegistry.getAllSession()
                .forEach(sseSession -> {
                    try {
                        sseSession.sendHeartbeat();
                    } catch (IOException e) {
                        sseSession.completeWithError(e);
                    }
                });
    }
}
