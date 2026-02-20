package com.github.mangila.fibonacci.jobrunr.job.consumer.compute;

import com.github.mangila.fibonacci.shared.FibonacciAlgorithm;
import com.github.mangila.fibonacci.jobrunr.job.model.FibonacciComputeRequest;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.postgres.test.PostgresTestContainer;
import com.github.mangila.fibonacci.redis.RedisKey;
import com.github.mangila.fibonacci.redis.test.RedisTestContainer;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.jdbc.JdbcTestUtils;
import redis.clients.jedis.UnifiedJedis;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;

@PostgresTestContainer
@RedisTestContainer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "app.job.consumer.enabled=true",
                "app.job.consumer.limit=50",
                "app.job.consumer.cron=0/15 * * * * *",
        })
class ComputeJobHandlerTest {

    @MockitoSpyBean
    private ThreadPoolTaskExecutor computeAsyncTaskExecutor;
    @MockitoSpyBean
    private PostgresRepository postgresRepository;
    @Autowired
    private UnifiedJedis jedis;
    @Autowired
    private JsonMapper jsonMapper;
    @Autowired
    private RedisKey queue;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Language("JSON")
    private String payload;

    @BeforeEach
    void setUp() {
        this.payload = jsonMapper.writeValueAsString(new FibonacciComputeRequest(10, FibonacciAlgorithm.ITERATIVE));
    }

    @Test
    void run() throws Exception {
        jedis.rpush(queue.value(), payload);
        var rows = JdbcTestUtils.countRowsInTable(jdbcTemplate, "fibonacci_results");
        assertThat(rows).isEqualTo(0);
        var inOrder = Mockito.inOrder(computeAsyncTaskExecutor, postgresRepository);
        await().atMost(60, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    inOrder.verify(computeAsyncTaskExecutor, times(1)).submitCompletable(any(ComputeTask.class));
                    inOrder.verify(postgresRepository, times(1)).insert(anyInt(), any(BigDecimal.class), anyInt());
                    inOrder.verify(postgresRepository, times(1)).upsertMetadata(any());
                    int count = JdbcTestUtils.countRowsInTable(jdbcTemplate, "fibonacci_results");
                    assertThat(count).isEqualTo(1);
                });

    }
}