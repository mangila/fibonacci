package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.PostgresTestContainerConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "app.fibonacci.algorithm=iterative",
                "app.fibonacci.offset=1",
                "app.fibonacci.limit=5",
                "app.fibonacci.delay=1s"
        })
@Import(PostgresTestContainerConfiguration.class)
class FibonacciSchedulerTest {

    @Autowired
    private FibonacciTask fibonacciTask;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SimpleAsyncTaskScheduler simpleAsyncTaskScheduler;

    @Test
    void insertComputeTask() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("FibonacciScheduler");
        await()
                .atMost(10, TimeUnit.SECONDS)
                .until(() -> fibonacciTask.isLimitReached() && !simpleAsyncTaskScheduler.isRunning());
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));

        var rows = JdbcTestUtils.countRowsInTable(jdbcTemplate, "fibonacci_results");
        assertThat(rows).isEqualTo(5);
    }
}