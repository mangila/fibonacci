package com.github.mangila.fibonacci.jobrunr.job.consumer.compute;

import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.postgres.test.PostgresTestContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;

@PostgresTestContainer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                // Producer
                "app.job.producer.enabled=true",
                "app.job.producer.limit=50",
                "app.job.producer.batchSize=100",
                // Consumer
                "app.job.consumer.enabled=true",
                "app.job.consumer.limit=50",
                "app.job.consumer.cron=0/30 * * * * *",
                // JobRunr
                "jobrunr.background-job-server.poll-interval-in-seconds=15"
        })
class ComputeJobHandlerTest {

    @MockitoSpyBean
    private ThreadPoolTaskExecutor computeAsyncTaskExecutor;
    @MockitoSpyBean
    private PostgresRepository postgresRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DisplayName("Wait and produce, consumer and compute some Fibonacci sequences with JobRunr and verify in database")
    @Test
    void run() throws Exception {
        var inOrder = Mockito.inOrder(computeAsyncTaskExecutor, postgresRepository);
        await().atMost(60, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    inOrder.verify(computeAsyncTaskExecutor, atLeastOnce()).submitCompletable(any(ComputeTask.class));
                    inOrder.verify(postgresRepository, atLeastOnce()).insert(anyInt(), any(BigDecimal.class), anyInt());
                    inOrder.verify(postgresRepository, atLeastOnce()).upsertMetadata(any());
                    int rows = JdbcTestUtils.countRowsInTable(jdbcTemplate, "fibonacci_results");
                    assertThat(rows).isGreaterThan(1);
                    rows = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                            "fibonacci_metadata",
                            "computed = true");
                    assertThat(rows).isGreaterThan(1);
                });

    }
}