package com.github.mangila.fibonacci.scheduler.repository;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.scheduler.PostgresTestContainer;
import com.github.mangila.fibonacci.scheduler.task.FibonacciComputeTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@PostgresTestContainer
@Import({FibonacciRepository.class})
class FibonacciRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private FibonacciRepository repository;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "fibonacci_results");
    }

    @Test
    @DisplayName("Save and verify Fibonacci sequence")
    void test() {
        var task = new FibonacciComputeTask(FibonacciAlgorithm.ITERATIVE, 10);
        var result = task.call();
        repository.save(result);
        var rows = JdbcTestUtils.countRowsInTable(jdbcTemplate, "fibonacci_results");
        assertThat(rows).isEqualTo(1);
        assertThat(repository.hasSequences(List.of(1, 2, 3, 4, 5, 10))).hasSize(1);
        assertThat(repository.hasSequences(List.of(1, 2, 3, 4, 5))).isEmpty();
        assertThat(repository.hasSequences(List.of())).isEmpty();
    }

}