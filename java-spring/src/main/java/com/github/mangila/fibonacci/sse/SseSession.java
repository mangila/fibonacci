package com.github.mangila.fibonacci.sse;

import io.github.mangila.ensure4j.Ensure;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public record SseSession(String username,
                         AtomicBoolean livestream,
                         SseEmitter emitter) {

    public SseSession {
        Ensure.notNull(username);
        Ensure.notNull(livestream);
        Ensure.notBlank(username);
        Ensure.notNull(emitter);
    }

    public void send(String eventName, Object payload) throws IOException {
        var event = SseEmitter.event()
                .id(username)
                .name(eventName)
                .data(payload, MediaType.APPLICATION_JSON)
                .build();
        emitter.send(event);
    }

    public void complete() {
        emitter.complete();
    }

    public void completeWithError(Throwable throwable) {
        emitter.completeWithError(throwable);
    }

    public boolean isLivestream() {
        return livestream.get();
    }

    public void setLivestream(boolean livestream) {
        this.livestream.set(livestream);
    }
}
