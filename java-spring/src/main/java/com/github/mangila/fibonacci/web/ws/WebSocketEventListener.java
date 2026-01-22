package com.github.mangila.fibonacci.web.ws;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;

@Component
public class WebSocketEventListener {

    private static final Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpUserRegistry userRegistry;

    public WebSocketEventListener(SimpUserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        var username = getUser(event.getUser());
        log.info("WebSocket connection established for user: {}", username);
        log.info("Total connected users: {}", userRegistry.getUserCount());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        var username = getUser(event.getUser());
        log.info("WebSocket connection closed for user: {}", username);
        log.info("Total connected users: {}", userRegistry.getUserCount());
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        var username = getUser(event.getUser());
        var destination = event.getMessage().getHeaders().get("simpDestination");
        log.info("User {} subscribed to destination: {}", username, destination);
    }

    @EventListener
    public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
        var username = getUser(event.getUser());
        log.info("User {} unsubscribed", username);
    }

    @NonNull
    private String getUser(Principal principal) {
        if (principal == null) {
            return "unknown";
        }
        return principal.getName();
    }
}