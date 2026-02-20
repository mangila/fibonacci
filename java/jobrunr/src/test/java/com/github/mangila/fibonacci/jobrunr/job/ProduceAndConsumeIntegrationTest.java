package com.github.mangila.fibonacci.jobrunr.job;

import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.postgres.test.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@PostgresTestContainer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                // Producer
                "app.job.producer.enabled=true",
                "app.job.producer.limit=10",
                "app.job.producer.batchSize=100",
                // Consumer
                "app.job.consumer.enabled=true",
                "app.job.consumer.limit=10",
                "app.job.consumer.cron=0/20 * * * * *",
                // JobRunr
                "jobrunr.background-job-server.poll-interval-in-seconds=15"
        })
public class ProduceAndConsumeIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoSpyBean
    private PostgresRepository postgresRepository;

    @Test
    void test() {
        await()
                .atMost(Duration.ofSeconds(60))
                .untilAsserted(() -> {
                    int rows = JdbcTestUtils.countRowsInTable(jdbcTemplate, "fibonacci_results");
                    assertThat(rows).isGreaterThan(1);
                    rows = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                            "fibonacci_metadata",
                            "scheduled = true AND computed = true");
                    assertThat(rows).isGreaterThan(1);
                });
    }

}
