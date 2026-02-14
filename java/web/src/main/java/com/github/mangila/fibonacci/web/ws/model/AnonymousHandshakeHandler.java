package com.github.mangila.fibonacci.web.ws.model;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

public class AnonymousHandshakeHandler extends DefaultHandshakeHandler {


    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        String anonymousId = "guest_" + UUID.randomUUID();
        logger.info("Generated anonymous ID: %s".formatted(anonymousId));
        return () -> anonymousId;
    }
}
