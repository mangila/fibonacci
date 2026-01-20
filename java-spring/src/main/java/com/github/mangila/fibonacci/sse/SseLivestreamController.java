package com.github.mangila.fibonacci.sse;

import com.github.mangila.fibonacci.event.PgNotificationPayload;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("api/v1/sse/livestream")
public class SseLivestreamController {

    private static final Logger log = LoggerFactory.getLogger(SseLivestreamController.class);

    private final SseEmitterRegistry emitterRegistry;

    public SseLivestreamController(@Qualifier("livestreamEmitterRegistry") SseEmitterRegistry emitterRegistry) {
        this.emitterRegistry = emitterRegistry;
    }

    @EventListener
    public void sseLivestream(PgNotificationPayload payload) {
        emitterRegistry.getAllSession()
                .forEach(sseSession -> {
                    try {
                        sseSession.send("livestream", payload.value());
                    } catch (Exception e) {
                        sseSession.completeWithError(e);
                    }
                });
    }

    @GetMapping
    public ResponseEntity<SseEmitter> subscribeLivestream(@RequestParam String streamKey, HttpSession httpSession) {
        log.info("Received request for subscription to {} - {}", httpSession.getId(), streamKey);
        SseSession session = emitterRegistry.subscribe(httpSession.getId(), streamKey);
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                // nginx buffer stuffs
                .header("X-Accel-Buffering", "no")
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(session.emitter());
    }
}
