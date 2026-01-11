package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.PostgresTestContainerConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(PostgresTestContainerConfiguration.class)
class FibonacciSchedulerTest {

    @MockitoBean
    private SimpleAsyncTaskScheduler taskScheduler;

    @Autowired
    private FibonacciScheduler fibonacciScheduler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void insertComputeBatch() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("FibonacciScheduler");
        fibonacciScheduler.insertComputeBatch();
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));

        var rows = JdbcTestUtils.countRowsInTable(jdbcTemplate, "fibonacci_results");
        assertThat(rows).isEqualTo(102);
    }
}