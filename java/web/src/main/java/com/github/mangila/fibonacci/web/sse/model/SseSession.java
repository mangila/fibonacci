package com.github.mangila.fibonacci.web.sse.model;

import io.github.mangila.ensure4j.Ensure;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;

public record SseSession(SseContext context, SseEmitter emitter) {

    private static final Set<ResponseBodyEmitter.DataWithMediaType> HEART_BEAT_MESSAGE = SseEmitter.event()
            .comment("heartbeat")
            .build();

    public SseSession {
        Ensure.notNull(context);
        Ensure.notNull(emitter);
    }

    public void send(String eventName, String id, Object payload) throws IOException {
        var event = SseEmitter.event()
                .id(id)
                .name(eventName)
                .data(payload, MediaType.APPLICATION_JSON)
                .reconnectTime(Duration.ofSeconds(5).toMillis())
                .comment(context().streamKey())
                .comment(context().channel())
                .build();
        emitter.send(event);
    }

    public void sendHeartbeat() throws IOException {
        emitter.send(HEART_BEAT_MESSAGE);
    }

    public void complete() {
        emitter.complete();
    }

    public void completeWithError(Throwable throwable) {
        emitter.completeWithError(throwable);
    }
}
