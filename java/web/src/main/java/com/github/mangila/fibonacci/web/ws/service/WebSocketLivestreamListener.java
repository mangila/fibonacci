package com.github.mangila.fibonacci.web.ws.service;

import com.github.mangila.fibonacci.postgres.FibonacciProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
public class WebSocketLivestreamListener {

    private static final Logger log = LoggerFactory.getLogger(WebSocketLivestreamListener.class);
    private final SimpMessagingTemplate template;
    private final SimpUserRegistry registry;

    public WebSocketLivestreamListener(SimpMessagingTemplate template,
                                       SimpUserRegistry registry) {
        this.template = template;
        this.registry = registry;
    }

    @EventListener
    public void wsLivestream(FibonacciProjection projection) {
        log.info("Received fibonacci projection: {}", projection);
    }

}
