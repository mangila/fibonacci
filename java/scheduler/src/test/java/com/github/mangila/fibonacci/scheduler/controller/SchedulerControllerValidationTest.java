package com.github.mangila.fibonacci.scheduler.controller;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.core.model.FibonacciComputeCommand;
import com.github.mangila.fibonacci.scheduler.scheduler.Scheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.stream.Stream;

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
    }

    @Test
    @DisplayName("should return accepted")
    void testAccepted() {
        var command = new FibonacciComputeCommand(FibonacciAlgorithm.ITERATIVE, 1, 10);
        restTestClient.post()
                .uri("/api/v1/scheduler")
                .contentType(MediaType.APPLICATION_JSON)
                .body(command)
                .exchange()
                .expectStatus().isAccepted();
    }

    @ParameterizedTest
    @MethodSource("invalidCommands")
    void shouldReturnBadRequestWhenCommandIsInvalid(String rawJson) {
        restTestClient.post()
                .uri("/api/v1/scheduler")
                .contentType(MediaType.APPLICATION_JSON)
                .body(rawJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .value(problemDetail -> {
                    System.out.println(problemDetail);
                });
    }

    private static Stream<String> invalidCommands() {
        // language=JSON
        String malformedMissingBrace = """
                {"algorithm":"ITERATIVE","start":1,"end":10
                """;

        // language=JSON
        String malformedTrailingComma = """
                {"algorithm":"ITERATIVE","start":1,"end":10},
                """;

        // language=JSON
        String notAnObjectNull = """
                null
                """;

        // language=JSON
        String notAnObjectArray = """
                []
                """;

        // language=JSON
        String missingAllFields = """
                {}
                """;

        // language=JSON
        String missingAlgorithm = """
                {"start":1,"end":10}
                """;

        // language=JSON
        String algorithmIsNull = """
                {"algorithm":null,"start":1,"end":10}
                """;

        // language=JSON
        String unknownEnumValue = """
                {"algorithm":"NOT_A_REAL_ALGO","start":1,"end":10}
                """;

        // language=JSON
        String wrongTypeForAlgorithm = """
                {"algorithm":123,"start":1,"end":10}
                """;

        // language=JSON
        String wrongTypeForStart = """
                {"algorithm":"ITERATIVE","start":"one","end":10}
                """;

        // language=JSON
        String startTooSmall = """
                {"algorithm":"ITERATIVE","start":0,"end":10}
                """;

        // language=JSON
        String startGreaterThanEnd = """
                {"algorithm":"ITERATIVE","start":5,"end":3}
                """;

        return Stream.of(
                malformedMissingBrace,
                malformedTrailingComma,
                notAnObjectNull,
                notAnObjectArray,
                missingAllFields,
                missingAlgorithm,
                algorithmIsNull,
                unknownEnumValue,
                wrongTypeForAlgorithm,
                wrongTypeForStart,
                startTooSmall,
                startGreaterThanEnd
        );
    }
}
