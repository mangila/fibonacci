package com.github.mangila.fibonacci.web.sse.service;

import com.github.mangila.fibonacci.postgres.FibonacciProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class SseLivestreamListener {

    private static final Logger log = LoggerFactory.getLogger(SseLivestreamListener.class);
    private final SseSessionRegistry registry;

    public SseLivestreamListener(SseSessionRegistry registry) {
        this.registry = registry;
    }

    @EventListener
    public void sseLivestream(FibonacciProjection projection) {
        log.info("Received fibonacci projection: {}", projection);
    }

}
