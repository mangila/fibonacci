package com.github.mangila.fibonacci.sse;

import com.github.mangila.fibonacci.db.FibonacciRepository;
import com.github.mangila.fibonacci.event.PgNotificationCollection;
import com.github.mangila.fibonacci.model.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
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
    private final FibonacciMapper mapper;
    private final SseEmitterRegistry emitterRegistry;

    public SseController(FibonacciRepository repository,
                         FibonacciMapper mapper,
                         SseEmitterRegistry emitterRegistry) {
        this.repository = repository;
        this.mapper = mapper;
        this.emitterRegistry = emitterRegistry;
    }

    @EventListener
    public void sseLivestream(PgNotificationCollection payload) {
        emitterRegistry.getAllSession()
                .forEach(sseSession -> {
                    try {
                        sseSession.send("livestream", payload.value());
                    } catch (Exception e) {
                        sseSession.completeWithError(e);
                    }
                });
    }

    @GetMapping("{channel}")
    public ResponseEntity<SseEmitter> sseSubscribe(
            @PathVariable String channel,
            @RequestParam String streamKey) {
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
        SseSession session = emitterRegistry.getSession(channel, streamKey);
        FibonacciResultEntity result = repository.queryById(id);
        FibonacciDto dto = mapper.map(result);
        try {
            session.send("id", dto);
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
        SseSession session = emitterRegistry.getSession(channel, streamKey);
        List<FibonacciResultProjection> projections = repository.queryForList(option);
        List<FibonacciDto> dtos = projections.stream()
                .map(mapper::map)
                .toList();
        try {
            session.send("list", dtos);
        } catch (IOException e) {
            session.completeWithError(e);
        }
    }
}
