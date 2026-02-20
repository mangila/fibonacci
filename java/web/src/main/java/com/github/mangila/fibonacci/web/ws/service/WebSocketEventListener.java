package com.github.mangila.fibonacci.web.ws.service;

import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.ops.EnsureStringOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;

@Component
public class WebSocketEventListener {

    private static final Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);
    private static final EnsureStringOps ENSURE_STRING_OPS = Ensure.strings();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        String username = getUsernameSafely(event.getUser());
        log.info("Received a new web socket connection from user '{}'", username);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String username = getUsernameSafely(event.getUser());
        log.info("User '{}' disconnected", username);
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
    }

    @EventListener
    public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
    }

    private static String getUsernameSafely(Principal principal) {
        Ensure.notNull(principal, "Principal must not be null");
        Ensure.notNull(principal.getName(), "Principal name must not be null");
        var username = principal.getName();
        return ENSURE_STRING_OPS.notBlank(username, "Username must not be blank");
    }
}
