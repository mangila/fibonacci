package com.github.mangila.fibonacci.web.ws.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WsRedisMessageHandler {

    private final SimpMessagingTemplate template;

    public WsRedisMessageHandler(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void handleWsMessage(String message, String channel) {
    }

}
