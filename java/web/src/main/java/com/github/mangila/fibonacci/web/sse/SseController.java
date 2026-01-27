package com.github.mangila.fibonacci.web.sse;

import com.github.mangila.fibonacci.web.dto.*;
import com.github.mangila.fibonacci.web.repository.FibonacciRepository;
import com.github.mangila.fibonacci.web.sse.model.SseSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

/**
 * Adapted for microsoft-sse-fetcher client
 */
@RestController
@RequestMapping("api/v1/sse")
@Validated
public class SseController {

    private static final Logger log = LoggerFactory.getLogger(SseController.class);

    private final SimpleAsyncTaskExecutor ioAsyncTaskExecutor;
    private final FibonacciDtoMapper mapper;
    private final FibonacciRepository repository;
    private final SseEmitterRegistry emitterRegistry;

    public SseController(SimpleAsyncTaskExecutor ioAsyncTaskExecutor,
                         FibonacciDtoMapper mapper,
                         FibonacciRepository repository,
                         SseEmitterRegistry emitterRegistry) {
        this.ioAsyncTaskExecutor = ioAsyncTaskExecutor;
        this.mapper = mapper;
        this.repository = repository;
        this.emitterRegistry = emitterRegistry;
    }


    @GetMapping("favicon.ico")
    @ResponseBody
    void doNothing() {
        // do nothing
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SseEmitter> sseSubscribe(@RequestBody @NotNull @Valid SseSubscription subscription) {
        SseSession session = emitterRegistry.subscribe(subscription);
        log.info("New session: {}", session);
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                // nginx buffer stuffs
                .header("X-Accel-Buffering", "no")
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(session.emitter());
    }

    @PostMapping(value = "/id", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sseQueryById(@RequestBody @NotNull @Valid SseFibonacciQuery query) {
        SseSession session = emitterRegistry.getSession(query.subscription());
        FibonacciDto dto = repository.queryById(query.id())
                .map(mapper::map)
                .orElseThrow();
        try {
            session.send("id", dto.sequence(), dto);
        } catch (IOException e) {
            session.completeWithError(e);
        }
    }

    @PostMapping(value = "/stream", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sseQueryForList(@RequestBody @NotNull @Valid SseFibonacciStreamQuery query) {
        SseSession session = emitterRegistry.getSession(query.subscription());
        ioAsyncTaskExecutor.submitCompletable(new FibonacciStreamTask(ioAsyncTaskExecutor, mapper, repository, query, session));
    }
}
