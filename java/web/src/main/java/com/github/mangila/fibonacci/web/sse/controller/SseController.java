package com.github.mangila.fibonacci.web.sse.controller;

import com.github.mangila.fibonacci.web.sse.model.SseSubscription;
import com.github.mangila.fibonacci.web.sse.service.SseService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("api/v1/sse")
public class SseController {

    private final SseService service;

    public SseController(SseService service) {
        this.service = service;
    }

    @PostMapping
    public SseEmitter subscribe(@RequestBody SseSubscription subscription) {
        return service.subscribe(subscription);
    }
}
