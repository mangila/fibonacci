package com.github.mangila.fibonacci.scheduler.controller;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.core.model.FibonacciCommand;
import com.github.mangila.fibonacci.scheduler.PostgresTestContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
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
class SchedulerControllerTest {

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
    void shouldScheduleFibonacciCalculation() {
        int limit = 10;
        var command = new FibonacciCommand(FibonacciAlgorithm.ITERATIVE, 1, limit, 100);

        restTestClient.post()
                .uri("/api/v1/scheduler")
                .contentType(MediaType.APPLICATION_JSON)
                .body(command)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(Object.class)
                .value(body -> {
                    assertThat(body).isNotNull();
                    assertThat(body.toString()).contains("job-id");
                });
        await()
                .atMost(Duration.ofSeconds(30))
                .until(() -> {
                    return JdbcTestUtils.countRowsInTable(jdbcTemplate, "fibonacci_results") == limit;
                });
    }

    @Test
    void shouldReturnBadRequestWhenCommandIsInvalid() {
        var command = new FibonacciCommand(null, -1, 0, -1);

        restTestClient.post()
                .uri("/api/v1/scheduler")
                .contentType(MediaType.APPLICATION_JSON)
                .body(command)
                .exchange()
                .expectStatus().isBadRequest();
    }
}