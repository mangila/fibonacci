package com.github.mangila.fibonacci.web.sse;

import com.github.mangila.fibonacci.core.annotation.AlphaNumeric;
import com.github.mangila.fibonacci.core.model.FibonacciQuery;
import com.github.mangila.fibonacci.web.model.FibonacciDto;
import com.github.mangila.fibonacci.web.model.SseSession;
import com.github.mangila.fibonacci.web.service.FibonacciService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UUID;
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

@RestController
@RequestMapping("api/v1/sse")
@Validated
public class SseController {

    private static final Logger log = LoggerFactory.getLogger(SseController.class);

    private final SimpleAsyncTaskExecutor ioAsyncTaskExecutor;
    private final FibonacciService service;
    private final SseEmitterRegistry emitterRegistry;

    public SseController(SimpleAsyncTaskExecutor ioAsyncTaskExecutor,
                         FibonacciService service,
                         SseEmitterRegistry emitterRegistry) {
        this.ioAsyncTaskExecutor = ioAsyncTaskExecutor;
        this.service = service;
        this.emitterRegistry = emitterRegistry;
    }

    @GetMapping("{channel}")
    public ResponseEntity<SseEmitter> sseSubscribe(
            @AlphaNumeric @PathVariable String channel,
            @UUID @RequestParam String streamKey) {
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
            @AlphaNumeric @PathVariable String channel,
            @UUID @RequestParam String streamKey,
            @Min(1) @RequestParam int id) {
        SseSession session = emitterRegistry.getSession(channel, streamKey);
        FibonacciDto dto = service.queryById(id);
        try {
            session.send("id", dto);
        } catch (IOException e) {
            session.completeWithError(e);
        }
    }

    @PostMapping("{channel}/stream")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sseQueryForList(
            @AlphaNumeric @PathVariable String channel,
            @UUID @RequestParam String streamKey,
            @RequestBody @Valid @NotNull FibonacciQuery query) {
        SseSession session = emitterRegistry.getSession(channel, streamKey);
        ioAsyncTaskExecutor.submitCompletable(() -> {
            service.streamForList(query, stream -> {
                stream.forEach(projection -> {
                    try {
                        session.send("stream", projection);
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (IOException | InterruptedException e) {
                        Thread.currentThread().interrupt();
                        session.completeWithError(e);
                    }
                });
            });
        });
    }
}
