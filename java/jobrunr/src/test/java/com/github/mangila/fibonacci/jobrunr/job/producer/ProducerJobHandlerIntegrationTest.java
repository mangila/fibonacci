package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.postgres.test.PostgresTestContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@PostgresTestContainer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "app.job.producer.enabled=true",
                "app.job.producer.limit=50",
                "app.job.producer.batchSize=10",
                "jobrunr.background-job-server.poll-interval-in-seconds=15"
        })
class ProducerJobHandlerIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoSpyBean
    private PostgresRepository postgresRepository;

    @DisplayName("Wait and produce some Fibonacci sequences with JobRunr and verify in database")
    @Test
    void test() {
        await()
                .atMost(Duration.ofSeconds(60))
                .untilAsserted(() -> {
                    verify(postgresRepository, times(5)).batchInsertMetadata(anyList());
                    var rows = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                            "fibonacci_metadata",
                            "computed = false");
                    assertThat(rows).isEqualTo(50);
                });
    }
}