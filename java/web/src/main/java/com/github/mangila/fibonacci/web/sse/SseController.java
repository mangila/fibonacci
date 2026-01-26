package com.github.mangila.fibonacci.web.sse;

import com.github.mangila.fibonacci.core.entity.FibonacciEntity;
import com.github.mangila.fibonacci.web.repository.FibonacciRepository;
import com.github.mangila.fibonacci.web.sse.model.SseFibonacciQuery;
import com.github.mangila.fibonacci.web.sse.model.SseFibonacciStreamQuery;
import com.github.mangila.fibonacci.web.sse.model.SseSession;
import com.github.mangila.fibonacci.web.sse.model.SseSubscription;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Adapted for microsoft-sse-fetcher client
 */
@RestController
@RequestMapping("api/v1/sse")
@Validated
public class SseController {

    private static final Logger log = LoggerFactory.getLogger(SseController.class);

    private final SimpleAsyncTaskExecutor ioAsyncTaskExecutor;
    private final FibonacciRepository repository;
    private final SseEmitterRegistry emitterRegistry;

    public SseController(SimpleAsyncTaskExecutor ioAsyncTaskExecutor,
                         FibonacciRepository repository,
                         SseEmitterRegistry emitterRegistry) {
        this.ioAsyncTaskExecutor = ioAsyncTaskExecutor;
        this.repository = repository;
        this.emitterRegistry = emitterRegistry;
    }


    @PostMapping
    public ResponseEntity<SseEmitter> sseSubscribe(@Valid @RequestBody SseSubscription subscription) {
        SseSession session = emitterRegistry.subscribe(subscription);
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                // nginx buffer stuffs
                .header("X-Accel-Buffering", "no")
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(session.emitter());
    }

    @PostMapping("/id")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sseQueryById(@RequestBody @Valid SseFibonacciQuery query) {
        SseSession session = emitterRegistry.getSession(query.subscription());
        FibonacciEntity entity = repository.queryById(query.id()).orElseThrow();
        try {
            session.send("id", String.valueOf(entity.sequence()), entity);
        } catch (IOException e) {
            session.completeWithError(e);
        }
    }

    @PostMapping("/stream")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sseQueryForList(@Valid SseFibonacciStreamQuery query) {
        SseSession session = emitterRegistry.getSession(query.subscription());
        ioAsyncTaskExecutor.submitCompletable(() -> {
            repository.streamForList(query.offset(), query.limit(), stream -> {
                stream.forEach(projection -> {
                    try {
                        session.send("stream", String.valueOf(projection.sequence()), projection);
                        TimeUnit.MILLISECONDS.sleep(query.delayInMillis());
                    } catch (IOException | InterruptedException e) {
                        Thread.currentThread().interrupt();
                        session.completeWithError(e);
                    }
                });
            });
        });
    }
}
