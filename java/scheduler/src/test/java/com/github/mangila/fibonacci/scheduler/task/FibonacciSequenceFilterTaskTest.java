package com.github.mangila.fibonacci.scheduler.task;

import com.github.mangila.fibonacci.core.model.FibonacciResult;
import com.github.mangila.fibonacci.scheduler.PostgresTestContainer;
import com.github.mangila.fibonacci.scheduler.repository.FibonacciRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@PostgresTestContainer
@Import(FibonacciRepository.class)
class FibonacciSequenceFilterTaskTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FibonacciRepository repository;

    @BeforeEach
    void setUp() {
        repository.insert(FibonacciResult.of(1, BigDecimal.ONE));
        repository.insert(FibonacciResult.of(2, BigDecimal.ONE));
        repository.insert(FibonacciResult.of(3, BigDecimal.ONE));
        repository.insert(FibonacciResult.of(4, BigDecimal.ONE));
        repository.insert(FibonacciResult.of(5, BigDecimal.ONE));
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "fibonacci_results");
    }

    @Test
    void run() {
        var task = new FibonacciSequenceFilterTask(repository, 1, 10);
        List<Integer> list = task.call();
        assertThat(list).hasSize(5);
        assertThat(list)
                .doesNotContain(1, 2, 3, 4, 5)
                .containsExactly(6, 7, 8, 9, 10);
    }

    @Test
    void runAllCompute() {
        var task = new FibonacciSequenceFilterTask(repository, 10, 20);
        List<Integer> list = task.call();
        assertThat(list).hasSize(20);

        task = new FibonacciSequenceFilterTask(repository, 1000,  3);
        list = task.call();
        assertThat(list).hasSize(3)
                .containsExactly(1000, 1001, 1002);
    }
}