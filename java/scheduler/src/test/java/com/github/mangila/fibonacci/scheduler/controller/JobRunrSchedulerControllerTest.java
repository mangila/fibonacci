package com.github.mangila.fibonacci.scheduler.controller;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.postgres.test.PostgresTestContainer;
import com.github.mangila.fibonacci.scheduler.model.FibonacciComputeCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@PostgresTestContainer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "jobrunr.background-job-server.poll-interval-in-seconds=5",
})
class JobRunrSchedulerControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RestTestClient restTestClient;

    @BeforeEach
    void setUp() {
        this.restTestClient = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    @DisplayName("should schedule Fibonacci calculation and wait for results")
    void test() {
        int end = 10;
        var command = new FibonacciComputeCommand(FibonacciAlgorithm.ITERATIVE, 1, end + 1);

        restTestClient.post()
                .uri("/api/v1/scheduler")
                .contentType(MediaType.APPLICATION_JSON)
                .body(command)
                .exchange()
                .expectStatus().isAccepted();
        await()
                .atMost(Duration.ofSeconds(30))
                .until(() -> {
                    return JdbcTestUtils.countRowsInTable(jdbcTemplate, "fibonacci_results") == end;
                });

        // cache run

        restTestClient.post()
                .uri("/api/v1/scheduler")
                .contentType(MediaType.APPLICATION_JSON)
                .body(command)
                .exchange()
                .expectStatus().isAccepted();
        await()
                .atMost(Duration.ofSeconds(30))
                .until(() -> {
                    return JdbcTestUtils.countRowsInTable(jdbcTemplate, "fibonacci_results") == end;
                });
    }

    @Test
    void validationFailTest() {
        // language=JSON
        String json = """
                    {"algorithm":"ITERATIVE","start":-1,"end":10}
                """;
        restTestClient.post()
                .uri("/api/v1/scheduler")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .value(problemDetail -> {
                    assertThat(problemDetail.getProperties().containsKey("errors"));
                    System.out.println(problemDetail);
                });
    }
}