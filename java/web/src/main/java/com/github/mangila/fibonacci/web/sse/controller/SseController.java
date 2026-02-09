package com.github.mangila.fibonacci.web.sse.controller;

import com.github.mangila.fibonacci.web.sse.model.SseIdQuery;
import com.github.mangila.fibonacci.web.sse.model.SseStreamQuery;
import com.github.mangila.fibonacci.web.sse.model.SseSubscription;
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

    private static final Map<String, String> OK = Map.of("status", "ok");

    private final SseService service;

    public SseController(SseService service) {
        this.service = service;
    }

    @PostMapping("subscribe")
    public SseEmitter sseSubscribe(@RequestBody @NotNull @Valid SseSubscription sseSubscription) {
        return service.subscribe(sseSubscription);
    }

    @PostMapping("stream")
    public ResponseEntity<Map<String, String>> sseQueryByStream(@RequestBody @NotNull @Valid SseStreamQuery streamQuery) {
        service.queryByStream(streamQuery);
        return ResponseEntity.ok(OK);
    }

    @PostMapping("id")
    public ResponseEntity<?> sseQueryById(@RequestBody @NotNull @Valid SseIdQuery sseIdQuery) {
        service.queryById(sseIdQuery);
        return ResponseEntity.ok(OK);
    }
}
