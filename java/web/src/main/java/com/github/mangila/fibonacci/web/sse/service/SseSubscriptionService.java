package com.github.mangila.fibonacci.web.sse.service;

import com.github.mangila.fibonacci.web.sse.model.SseSession;
import com.github.mangila.fibonacci.web.sse.model.SseSubscription;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class SseSubscriptionService {

    private final SseSessionRegistry registry;

    public SseSubscriptionService(SseSessionRegistry registry) {
        this.registry = registry;
    }

    public SseEmitter subscribe(SseSubscription sseSubscription) {
        var emitter = new SseEmitter(Long.MAX_VALUE);
        registry.add(new SseSession(sseSubscription, emitter));
        return emitter;
    }
}
