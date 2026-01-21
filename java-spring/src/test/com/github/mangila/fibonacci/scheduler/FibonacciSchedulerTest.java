package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.PostgresTestContainerConfiguration;
import com.github.mangila.fibonacci.db.FibonacciRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "app.fibonacci.algorithm=iterative",
                "app.fibonacci.offset=1",
                "app.fibonacci.limit=5",
                "app.fibonacci.delay=500ms"
        })
@Import(PostgresTestContainerConfiguration.class)
class FibonacciSchedulerTest {

    @Autowired
    private FibonacciTask fibonacciTask;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SimpleAsyncTaskScheduler simpleAsyncTaskScheduler;

    @MockitoSpyBean
    private FibonacciRepository repository;

    @Test
    void insertComputeTask() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("FibonacciScheduler");
        await()
                .atMost(5, TimeUnit.SECONDS)
                .until(() -> fibonacciTask.isLimitReached() && !simpleAsyncTaskScheduler.isRunning());
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));

        var rows = JdbcTestUtils.countRowsInTable(jdbcTemplate, "fibonacci_results");
        assertThat(rows).isEqualTo(5);
        verify(repository, times(6)).hasSequence(any(Integer.class));
        verify(repository, times(5)).insert(any());
    }
}