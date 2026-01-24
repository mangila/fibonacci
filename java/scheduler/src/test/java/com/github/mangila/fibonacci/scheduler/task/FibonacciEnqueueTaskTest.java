package com.github.mangila.fibonacci.scheduler.task;

import com.github.mangila.fibonacci.core.model.FibonacciResult;
import com.github.mangila.fibonacci.scheduler.PostgresTestContainer;
import com.github.mangila.fibonacci.scheduler.properties.FibonacciProperties;
import com.github.mangila.fibonacci.scheduler.repository.FibonacciRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@PostgresTestContainer
@Import({FibonacciEnqueueTask.class,
        FibonacciRepository.class,
        FibonacciProperties.class})
@TestPropertySource(properties = {"app.fibonacci.offset=1", "app.fibonacci.limit=10"})
class FibonacciEnqueueTaskTest {

    @Autowired
    private FibonacciEnqueueTask task;

    @Autowired
    private FibonacciRepository repository;

    @Test
    void run() {
        repository.save(FibonacciResult.of(1, new BigDecimal(1)));
        repository.save(FibonacciResult.of(10, new BigDecimal(55)));
        repository.save(FibonacciResult.of(5, new BigDecimal(5)));
        List<Integer> list = task.call();
        assertThat(list).hasSize(7);
        assertThat(list).doesNotContain(1, 5, 10);
    }
}