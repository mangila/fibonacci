package com.github.mangila.fibonacci.web.sse.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class SseLivestreamListener {

    private final SseSessionRegistry registry;

    public SseLivestreamListener(SseSessionRegistry registry) {
        this.registry = registry;
    }

    @EventListener
    public void sseLivestream() {
    }

}
