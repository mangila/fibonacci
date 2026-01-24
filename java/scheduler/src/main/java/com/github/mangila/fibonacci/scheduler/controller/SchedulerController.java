package com.github.mangila.fibonacci.scheduler.controller;

import com.github.mangila.fibonacci.core.model.FibonacciCommand;
import com.github.mangila.fibonacci.scheduler.scheduler.Scheduler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/scheduler")
@Validated
public class SchedulerController {

    private final Scheduler scheduler;

    public SchedulerController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, UUID>> startFibonacciCalculation(@Valid @NotNull @RequestBody FibonacciCommand command) {
        UUID uuid = scheduler.scheduleFibonacciCalculation(command);
        return ResponseEntity.accepted()
                .body(Map.of("job-id", uuid));
    }
}
