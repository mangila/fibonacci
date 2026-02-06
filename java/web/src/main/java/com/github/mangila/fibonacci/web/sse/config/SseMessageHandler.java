package com.github.mangila.fibonacci.web.sse.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class SseMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(SseMessageHandler.class);

    public void handleMessage(String message, String channel) {
        log.info("Received message: {}", message);
        log.info("Received channel: {}", channel);
    }
}
