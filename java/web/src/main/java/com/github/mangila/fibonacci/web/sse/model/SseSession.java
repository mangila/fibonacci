package com.github.mangila.fibonacci.web.sse.model;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public record SseSession(
        SseSubscription subscription,
        SseEmitter emitter
) {
}
