package com.github.mangila.fibonacci.web.sse.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class SseLivestreamListener {

    private final SseSessionRegistry registry;

    public SseLivestreamListener(SseSessionRegistry registry) {
        this.registry = registry;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void sseLivestream() {
    }

}
