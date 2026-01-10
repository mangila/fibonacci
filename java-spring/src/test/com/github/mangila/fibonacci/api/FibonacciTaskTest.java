package com.github.mangila.fibonacci.api;

import com.github.mangila.fibonacci.PostgresTestContainerConfiguration;
import com.github.mangila.fibonacci.db.FibonacciRepository;
import com.github.mangila.fibonacci.model.FibonacciOption;
import com.github.mangila.fibonacci.scheduler.FibonacciTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostgresTestContainerConfiguration.class)
class FibonacciTaskTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void call() {
        FibonacciRepository repository = new FibonacciRepository(jdbcTemplate);
        FibonacciOption option = new FibonacciOption(1, 1000);
        FibonacciTask task = new FibonacciTask(repository, option);

        var stopWatch = new StopWatch();
        stopWatch.start("FibonacciTask");
        task.run();
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.SECONDS));

        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "fibonacci_results"))
                .isEqualTo(option.limit());
    }
}
