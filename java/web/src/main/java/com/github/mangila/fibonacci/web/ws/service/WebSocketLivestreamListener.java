package com.github.mangila.fibonacci.web.ws.service;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
public class WebSocketLivestreamListener {

    private final SimpMessagingTemplate template;
    private final SimpUserRegistry registry;

    public WebSocketLivestreamListener(SimpMessagingTemplate template,
                                       SimpUserRegistry registry) {
        this.template = template;
        this.registry = registry;
    }

    @EventListener
    public void wsLivestream() {
    }

}
