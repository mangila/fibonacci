package com.github.mangila.fibonacci.web.sse;

import com.github.mangila.fibonacci.db.model.PgNotificationPayloadCollection;
import com.github.mangila.fibonacci.service.FibonacciService;
import com.github.mangila.fibonacci.shared.annotation.ValidChannel;
import com.github.mangila.fibonacci.web.dto.FibonacciDto;
import com.github.mangila.fibonacci.web.dto.FibonacciProjectionDto;
import com.github.mangila.fibonacci.web.dto.FibonacciQuery;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/sse")
@Validated
public class SseController {

    private static final Logger log = LoggerFactory.getLogger(SseController.class);

    private final FibonacciService service;
    private final SseEmitterRegistry emitterRegistry;

    public SseController(FibonacciService service, SseEmitterRegistry emitterRegistry) {
        this.service = service;
        this.emitterRegistry = emitterRegistry;
    }

    @EventListener
    public void sseLivestream(PgNotificationPayloadCollection payload) {
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
            @ValidChannel @PathVariable String channel,
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
            @ValidChannel @PathVariable String channel,
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

    @PostMapping("{channel}/list")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sseQueryForList(
            @ValidChannel @PathVariable String channel,
            @UUID @RequestParam String streamKey,
            @RequestBody @Valid @NotNull FibonacciQuery query) {
        SseSession session = emitterRegistry.getSession(channel, streamKey);
        List<FibonacciProjectionDto> dtos = service.queryForList(query);
        try {
            session.send("list", dtos);
        } catch (IOException e) {
            session.completeWithError(e);
        }
    }
}
