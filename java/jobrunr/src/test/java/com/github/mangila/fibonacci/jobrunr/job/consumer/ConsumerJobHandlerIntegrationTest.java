package com.github.mangila.fibonacci.jobrunr.job.consumer;

import com.github.mangila.fibonacci.jobrunr.job.consumer.compute.ComputeScheduler;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.postgres.test.PostgresTestContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
class ConsumerJobHandlerIntegrationTest {

    @MockitoSpyBean
    private PostgresRepository postgresRepository;

    @MockitoSpyBean
    private ComputeScheduler computeScheduler;

    @DisplayName("Wait for producer then consume and verify invocation count")
    @Test
    void run() {
        await()
                .atMost(Duration.ofSeconds(60))
                .untilAsserted(() -> {
                    verify(postgresRepository, times(1)).streamMetadataWhereComputedFalseLocked(Mockito.anyInt(), Mockito.any());
                    verify(computeScheduler, times(50)).schedule(Mockito.any());
                });
    }
}