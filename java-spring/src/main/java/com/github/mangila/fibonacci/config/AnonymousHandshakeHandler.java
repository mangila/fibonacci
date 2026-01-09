package com.github.mangila.fibonacci.config;

import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

public class AnonymousHandshakeHandler extends DefaultHandshakeHandler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AnonymousHandshakeHandler.class);

    @Override
    protected @Nullable Principal determineUser(ServerHttpRequest request,
                                                WebSocketHandler wsHandler,
                                                Map<String, Object> attributes) {
        String anonymousId = "guest_" + UUID.randomUUID();
        logger.info("Generated anonymous ID: {}", anonymousId);
        return () -> anonymousId;
    }
}