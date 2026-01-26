package com.github.mangila.fibonacci.scheduler.controller;

import com.github.mangila.fibonacci.core.model.FibonacciComputeCommand;
import com.github.mangila.fibonacci.scheduler.scheduler.Scheduler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/scheduler")
@Validated
public class SchedulerController {

    private final Scheduler scheduler;

    public SchedulerController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @GetMapping("favicon.ico")
    @ResponseBody
    void doNothing() {
        // do nothing
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> startFibonacciCalculation(@Valid @NotNull @RequestBody FibonacciComputeCommand command) {
        scheduler.scheduleFibonacciCalculations(command);
        return ResponseEntity.accepted().build();
    }
}
