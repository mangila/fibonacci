package com.github.mangila.fibonacci.web.sse.service;

import com.github.mangila.fibonacci.web.sse.model.SseSession;
import com.github.mangila.fibonacci.web.sse.model.SseSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseSubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SseSubscriptionService.class);

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
