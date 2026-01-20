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

//    private static final Logger log = LoggerFactory.getLogger(SseController.class);
//
//    private final FibonacciRepository repository;
//    private final SseEmitterRegistry emitterRegistry;
//
//    public SseController(FibonacciRepository repository,
//                         @Qualifier("queryEmitterRegistry") SseEmitterRegistry emitterRegistry) {
//        this.repository = repository;
//        this.emitterRegistry = emitterRegistry;
//    }
//
//    @GetMapping("{username}")
//    public ResponseEntity<SseEmitter> subscribe(@PathVariable String username) {
//        log.info("New livestream subscription from {}", username);
//        SseSession session = emitterRegistry.subscribe(username, streamKey);
//        return ResponseEntity.ok()
//                .header("Cache-Control", "no-cache, no-store, must-revalidate")
//                // nginx buffer stuffs
//                .header("X-Accel-Buffering", "no")
//                .contentType(MediaType.TEXT_EVENT_STREAM)
//                .body(session.emitter());
//    }
//
//    @DeleteMapping("{username}")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public void unsubscribe(@PathVariable String username) {
//        log.info("Received request for unsubscription from {}", username);
//        emitterRegistry.unsubscribe(username);
//    }
//
//    @GetMapping("id/{username}")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public void queryById(@PathVariable String username, @RequestParam int id) {
//        log.info("Received request for fibonacci sequence {} from {}", id, username);
//        SseSession session = emitterRegistry.getOrThrow(username);
//        FibonacciResultEntity result = repository.queryById(id);
//        try {
//            session.send("id", result);
//        } catch (IOException e) {
//            emitterRegistry.removeWithError(username, e);
//        }
//    }
//
//    @PostMapping("list/{username}")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public void queryForList(@PathVariable String username, @RequestBody @Valid @NotNull FibonacciOption option) {
//        log.info("Received request for fibonacci sequence {} from {}", option, username);
//        SseSession session = emitterRegistry.getOrThrow(username);
//        List<FibonacciResultEntity> result = repository.queryForList(option);
//        try {
//            session.send("list", result);
//        } catch (IOException e) {
//            emitterRegistry.removeWithError(username, e);
//        }
//    }
}
