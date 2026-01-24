package com.github.mangila.fibonacci.scheduler.controller;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.core.model.FibonacciCommand;
import com.github.mangila.fibonacci.scheduler.scheduler.Scheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(SchedulerController.class)
public class SchedulerControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Scheduler scheduler;

    private RestTestClient restTestClient;

    @BeforeEach
    void setUp() {
        this.restTestClient = RestTestClient.bindTo(mockMvc).build();
        when(scheduler.scheduleFibonacciCalculation(any())).thenReturn(UUID.randomUUID());
    }

    @ParameterizedTest
    @MethodSource("invalidCommands")
    void shouldReturnBadRequestWhenCommandIsInvalid(FibonacciCommand command) {
        restTestClient.post()
                .uri("/api/v1/scheduler")
                .contentType(MediaType.APPLICATION_JSON)
                .body(command)
                .exchange()
                .expectStatus().isBadRequest();
    }

    private static Stream<Arguments> invalidCommands() {
        return Stream.of(
                Arguments.of(new FibonacciCommand(null, 1, 1, 1)), // null algorithm
                Arguments.of(new FibonacciCommand(FibonacciAlgorithm.ITERATIVE, 0, 1, 100)), // offset < 1
                Arguments.of(new FibonacciCommand(FibonacciAlgorithm.ITERATIVE, 1_000_001, 1, 100)), // offset > 1,000,000
                Arguments.of(new FibonacciCommand(FibonacciAlgorithm.ITERATIVE, 1, 0, 50)), // limit < 1
                Arguments.of(new FibonacciCommand(FibonacciAlgorithm.ITERATIVE, 1, 1001, 50)), // limit > 1000
                Arguments.of(new FibonacciCommand(FibonacciAlgorithm.ITERATIVE, 1, 1, 49)), // delay < 50
                Arguments.of(new FibonacciCommand(FibonacciAlgorithm.ITERATIVE, 1, 1, 10001)) // delay > 10000
        );
    }

    @Test
    void shouldReturnAcceptedWhenCommandIsValid() {
        var command = new FibonacciCommand(FibonacciAlgorithm.ITERATIVE, 1, 1, 100);
        restTestClient.post()
                .uri("/api/v1/scheduler")
                .contentType(MediaType.APPLICATION_JSON)
                .body(command)
                .exchange()
                .expectStatus().isAccepted();
    }
}
