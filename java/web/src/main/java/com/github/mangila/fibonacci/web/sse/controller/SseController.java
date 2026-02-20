package com.github.mangila.fibonacci.web.sse.controller;

import com.github.mangila.fibonacci.web.sse.model.SseIdQuery;
import com.github.mangila.fibonacci.web.sse.model.SseStreamQuery;
import com.github.mangila.fibonacci.web.sse.model.SseSubscription;
import com.github.mangila.fibonacci.web.sse.service.SseSubscriptionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequestMapping("api/v1/sse")
public class SseController {

    private static final Logger log = LoggerFactory.getLogger(SseController.class);

    private static final Map<String, String> OK = Map.of("status", "ok");

    private final SseSubscriptionService service;

    public SseController(SseSubscriptionService service) {
        this.service = service;
    }

    @PostMapping("subscribe")
    public SseEmitter sseSubscribe(@RequestBody @NotNull @Valid SseSubscription sseSubscription) {
        log.info("SSE subscribe: {}", sseSubscription);
        return service.subscribe(sseSubscription);
    }
}
