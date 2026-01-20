package com.github.mangila.fibonacci.sse;

import io.github.mangila.ensure4j.Ensure;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

public record SseSession(
        String sessionId,
        String streamKey,
        SseEmitter emitter) {

    public SseSession {
        Ensure.notBlank(sessionId);
        Ensure.notBlank(streamKey);
        Ensure.notNull(emitter);
    }

    public void send(String eventName, Object payload) throws IOException {
        var event = SseEmitter.event()
                .id(streamKey)
                .name(eventName)
                .data(payload, MediaType.APPLICATION_JSON)
                .comment(sessionId)
                .build();
        emitter.send(event);
    }

    public void sendHeartbeat() throws IOException {
        emitter.send(SseEmitter.event().comment("heartbeat").build());
    }

    public void complete() {
        emitter.complete();
    }

    public void completeWithError(Throwable throwable) {
        emitter.completeWithError(throwable);
    }
}
