package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.postgres.test.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@PostgresTestContainer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "app.job.producer.enabled=true",
                "app.job.producer.limit=50",
                "app.job.producer.cron=0/15 * * * * *",
        })
class ProducerJobHandlerTest {

    @MockitoSpyBean
    private JsonMapper jsonMapper;

    @Test
    void run() {
        await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    verify(jsonMapper, times(50));
                });

    }
}