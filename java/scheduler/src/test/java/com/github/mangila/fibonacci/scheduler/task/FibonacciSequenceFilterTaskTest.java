package com.github.mangila.fibonacci.scheduler.task;

import com.github.mangila.fibonacci.core.model.FibonacciResult;
import com.github.mangila.fibonacci.scheduler.PostgresTestContainer;
import com.github.mangila.fibonacci.scheduler.repository.FibonacciRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@PostgresTestContainer
@Import(FibonacciRepository.class)
class FibonacciSequenceFilterTaskTest {

    @Autowired
    private FibonacciRepository repository;

    @Test
    void run() {
        repository.save(FibonacciResult.of(1, new BigDecimal(1)));
        repository.save(FibonacciResult.of(10, new BigDecimal(55)));
        repository.save(FibonacciResult.of(5, new BigDecimal(5)));
        repository.save(FibonacciResult.of(12, new BigDecimal(1234)));
        var task = new FibonacciSequenceFilterTask(repository, 1, 10);
        List<Integer> list = task.call();
        assertThat(list).hasSize(7);
        assertThat(list).doesNotContain(1, 5, 10, 12);
    }
}