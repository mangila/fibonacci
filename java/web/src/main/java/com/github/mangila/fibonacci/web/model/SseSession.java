package com.github.mangila.fibonacci.web.model;

import io.github.mangila.ensure4j.Ensure;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;

public record SseSession(
        String channel,
        String streamKey,
        SseEmitter emitter) {

    private static final Set<ResponseBodyEmitter.DataWithMediaType> HEART_BEAT_MESSAGE = SseEmitter.event()
            .comment("heartbeat")
            .build();

    public SseSession {
        Ensure.notBlank(channel);
        Ensure.notBlank(streamKey);
        Ensure.notNull(emitter);
    }

    public void send(String eventName, Object payload) throws IOException {
        var event = SseEmitter.event()
                .id(streamKey)
                .name(eventName)
                .data(payload, MediaType.APPLICATION_JSON)
                .reconnectTime(Duration.ofSeconds(5).toMillis())
                .comment(channel)
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
