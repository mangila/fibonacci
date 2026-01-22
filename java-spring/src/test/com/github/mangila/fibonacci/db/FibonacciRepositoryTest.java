package com.github.mangila.fibonacci.db;

import com.github.mangila.fibonacci.FibonacciAlgorithm;
import com.github.mangila.fibonacci.PostgresTestContainerConfiguration;
import com.github.mangila.fibonacci.db.model.FibonacciEntity;
import com.github.mangila.fibonacci.scheduler.FibonacciComputeTask;
import com.github.mangila.fibonacci.web.dto.FibonacciQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({PostgresTestContainerConfiguration.class, FibonacciRepository.class})
class FibonacciRepositoryTest {

    @Autowired
    private FibonacciRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        var sequenceTen = new FibonacciComputeTask(FibonacciAlgorithm.ITERATIVE, 10).call();
        var sequenceTwenty = new FibonacciComputeTask(FibonacciAlgorithm.ITERATIVE, 20).call();
        repository.insert(sequenceTen);
        repository.insert(sequenceTwenty);
        int rows = JdbcTestUtils.countRowsInTable(jdbcTemplate, "fibonacci_results");
        assertThat(rows).isEqualTo(2);
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "fibonacci_results");
    }

    @Test
    void queryById() {
        FibonacciEntity entity = repository.queryById(1).orElseThrow();
        assertThat(entity).isNotNull();
        assertThat(entity.id()).isEqualTo(1);
        assertThat(entity.result()).isEqualTo(new BigDecimal(55));
        assertThat(entity.precision()).isEqualTo(2);
    }

    @Test
    void queryByIdFail() {
        assertThat(repository.queryById(100)).isEmpty();
    }

    @Test
    void queryForList() {
        var l = repository.queryForList(new FibonacciQuery(0, 10));
        assertThat(l).isNotNull();
        assertThat(l).hasSize(2);
    }

    @Test
    void hasSequence() {
        assertThat(repository.hasSequence(10)).isTrue();
        assertThat(repository.hasSequence(20)).isTrue();
        assertThat(repository.hasSequence(74)).isFalse();
    }
}