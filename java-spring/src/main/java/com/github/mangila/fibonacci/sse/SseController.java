package com.github.mangila.fibonacci.sse;

import com.github.mangila.fibonacci.db.FibonacciRepository;
import com.github.mangila.fibonacci.model.FibonacciOption;
import com.github.mangila.fibonacci.model.FibonacciResultEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/sse")
public class SseController {

    private static final Logger log = LoggerFactory.getLogger(SseController.class);

    private final FibonacciRepository repository;
    private final SseEmitterRegistry emitterRegistry;

    public SseController(FibonacciRepository repository,
                         @Qualifier("queryEmitterRegistry") SseEmitterRegistry emitterRegistry) {
        this.repository = repository;
        this.emitterRegistry = emitterRegistry;
    }

    @GetMapping("{channel}")
    public ResponseEntity<SseEmitter> sseSubscribe(
            @PathVariable String channel,
            @RequestParam String streamKey) {
        log.info("Received request for subscription to {}:{}", channel, streamKey);
        SseSession session = emitterRegistry.subscribe(channel, streamKey);
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                // nginx buffer stuffs
                .header("X-Accel-Buffering", "no")
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(session.emitter());
    }

    @GetMapping("{channel}/id")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sseQueryById(
            @PathVariable String channel,
            @RequestParam String streamKey,
            @RequestParam int id) {
        log.info("Received request for fibonacci sequence {}:{}", streamKey, channel);
        SseSession session = emitterRegistry.getSession(channel, streamKey);
        FibonacciResultEntity result = repository.queryById(id);
        try {
            session.send("id", result);
        } catch (IOException e) {
            session.completeWithError(e);
        }
    }

    @PostMapping("{channel}/list")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sseQueryForList(
            @PathVariable String channel,
            @RequestParam String streamKey,
            @RequestBody @Valid @NotNull FibonacciOption option) {
        log.info("Received request for fibonacci sequence {}:{}", streamKey, channel);
        SseSession session = emitterRegistry.getSession(channel, streamKey);
        List<FibonacciResultEntity> result = repository.queryForList(option);
        try {
            session.send("list", result);
        } catch (IOException e) {
            session.completeWithError(e);
        }
    }
}
