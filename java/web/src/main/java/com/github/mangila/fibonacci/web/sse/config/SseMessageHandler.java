package com.github.mangila.fibonacci.web.sse.config;

import com.github.mangila.fibonacci.web.sse.model.SseQuery;
import com.github.mangila.fibonacci.web.sse.service.SseSessionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class SseMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(SseMessageHandler.class);

    private final SseSessionRegistry registry;

    public SseMessageHandler(SseSessionRegistry registry) {
        this.registry = registry;
    }

    public void handleMessage(SseQuery message, String channel) {
        log.info("Received message: {}", message);
        log.info("Received channel: {}", channel);
    }

}
