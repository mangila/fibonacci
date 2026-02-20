package com.github.mangila.fibonacci.web.sse.service;

import com.github.mangila.fibonacci.postgres.FibonacciProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;

public class SseLivestreamListener {

    private static final Logger log = LoggerFactory.getLogger(SseLivestreamListener.class);

    private final SseSessionRegistry registry;

    public SseLivestreamListener(SseSessionRegistry registry) {
        this.registry = registry;
    }

    @EventListener
    public void sseLivestream(FibonacciProjection projection) {
        if (log.isDebugEnabled()) {
            log.debug("Received fibonacci projection: {}", projection);
        }
        registry.getAllSessions()
                .forEach(session -> session.send(projection));
    }
}
