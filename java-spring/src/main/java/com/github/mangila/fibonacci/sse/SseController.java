package com.github.mangila.fibonacci.sse;

import com.github.mangila.fibonacci.db.FibonacciRepository;
import com.github.mangila.fibonacci.event.PgNotificationPayload;
import com.github.mangila.fibonacci.model.FibonacciOption;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/sse/fibonacci")
public class SseController {

    private static final Logger log = LoggerFactory.getLogger(SseController.class);
    private final FibonacciRepository repository;
    private final SseEmitterRegistry emitterRegistry;

    public SseController(FibonacciRepository repository,
                         SseEmitterRegistry emitterRegistry) {
        this.repository = repository;
        this.emitterRegistry = emitterRegistry;
    }

    @EventListener
    public void sseLivestream(PgNotificationPayload payload) {
        var values = emitterRegistry.asMap().values();
        for (SseSession session : values) {
            if (session.livestream().get()) {
                try {
                    session.send("livestream", payload.value());
                } catch (IOException e) {
                    emitterRegistry.removeWithError(session.username(), e);
                }
            }
        }
    }

    @GetMapping("subscribe/{username}")
    public SseEmitter subscribe(@PathVariable String username) {
        return emitterRegistry.subscribe(username);
    }

    @GetMapping("subscribe/livestream/{username}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void subscribeLivestream(@PathVariable String username) {
        emitterRegistry.subscribeLivestream(username);
    }

    @DeleteMapping("subscribe/{username}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void unsubscribe(@PathVariable String username) {
        emitterRegistry.unsubscribe(username);
    }

    @DeleteMapping("subscribe/livestream/{username}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void unsubscribeLivestream(@PathVariable String username) {
        emitterRegistry.unsubscribeLivestream(username);
    }

    @PostMapping("{username}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void queryForOption(@PathVariable String username, @RequestBody @Valid @NotNull FibonacciOption option) {
        log.info("Received request for fibonacci sequence {} from {}", option, username);
        SseSession session = emitterRegistry.getOrThrow(username);
        var result = repository.queryForList(option);
        try {
            session.send("list", result);
        } catch (IOException e) {
            emitterRegistry.removeWithError(username, e);
        }
    }

    @GetMapping("{username}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void queryById(@PathVariable String username, @RequestParam int id) {
        log.info("Received request for fibonacci sequence {} from {}", id, username);
        SseSession session = emitterRegistry.getOrThrow(username);
        var result = repository.queryById(id);
        try {
            session.send("id", result);
        } catch (IOException e) {
            emitterRegistry.removeWithError(username, e);
        }
    }
}
