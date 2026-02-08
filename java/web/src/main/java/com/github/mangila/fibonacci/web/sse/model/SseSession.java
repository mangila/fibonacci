package com.github.mangila.fibonacci.web.sse.model;

import com.github.mangila.fibonacci.postgres.FibonacciProjection;
import com.github.mangila.fibonacci.web.shared.FibonacciDto;
import io.github.mangila.ensure4j.Ensure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;

public record SseSession(
        SseSubscription sseSubscription,
        SseEmitter emitter
) {
    private static final Logger log = LoggerFactory.getLogger(SseSession.class);

    private static final Set<ResponseBodyEmitter.DataWithMediaType> HEART_BEAT_EVENT = SseEmitter.event()
            .comment("heartbeat")
            .reconnectTime(1000L)
            .build();

    public SseSession {
        Ensure.notNull(sseSubscription, "Subscription must not be null");
        Ensure.notNull(emitter, "Emitter must not be null");
    }

    public void send(FibonacciProjection event) {
        try {
            final var sseEvent = SseEmitter.event()
                    .id(String.valueOf(event.sequence()))
                    .data(event, MediaType.APPLICATION_JSON)
                    .name(sseSubscription.channel())
                    .reconnectTime(1000L)
                    .comment(sseSubscription().username())
                    .build();
            emitter.send(sseEvent);
        } catch (IOException e) {
            log.error("Error while sending event: {} -- {}", sseSubscription, event, e);
        }
    }

    public void send(FibonacciDto event) {
        try {
            final var sseEvent = SseEmitter.event()
                    .id(String.valueOf(event.sequence()))
                    .data(event, MediaType.APPLICATION_JSON)
                    .name(sseSubscription.channel())
                    .reconnectTime(1000L)
                    .comment(sseSubscription().username())
                    .build();
            emitter.send(sseEvent);
        } catch (IOException e) {
            log.error("Error while sending event: {} -- {}", sseSubscription, event, e);
        }
    }

    public void sendHeartbeat() {
        try {
            emitter.send(HEART_BEAT_EVENT);
        } catch (IOException e) {
            log.error("Error while sending heartbeat: {}", e.getMessage(), e);
        }
    }
}
