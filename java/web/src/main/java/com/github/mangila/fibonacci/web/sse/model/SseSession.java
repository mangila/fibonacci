package com.github.mangila.fibonacci.web.sse.model;

import com.github.mangila.fibonacci.postgres.FibonacciProjection;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

public record SseSession(
        SseSubscription subscription,
        SseEmitter emitter
) {
    public void send(FibonacciProjection event) {
        try {
            final var sseEvent = SseEmitter.event()
                    .id(String.valueOf(event.sequence()))
                    .data(event, MediaType.APPLICATION_JSON)
                    .name(subscription.channel())
                    .reconnectTime(1000L)
                    .comment(subscription().username())
                    .build();
            emitter.send(sseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
