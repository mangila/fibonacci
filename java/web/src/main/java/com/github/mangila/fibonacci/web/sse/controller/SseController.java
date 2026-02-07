package com.github.mangila.fibonacci.web.sse.controller;

import com.github.mangila.fibonacci.web.sse.model.SseQueryRequest;
import com.github.mangila.fibonacci.web.sse.model.Subscription;
import com.github.mangila.fibonacci.web.sse.service.SseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

    private final SseService service;

    public SseController(SseService service) {
        this.service = service;
    }

    @PostMapping("subscribe")
    public SseEmitter sseSubscribe(@RequestBody @NotNull @Valid Subscription subscription) {
        return service.subscribe(subscription);
    }

    @PostMapping("query")
    public ResponseEntity<Map<String, String>> sseQuery(@RequestBody @NotNull @Valid SseQueryRequest request) {
        service.query(request);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
