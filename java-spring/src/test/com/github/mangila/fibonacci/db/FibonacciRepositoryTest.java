package com.github.mangila.fibonacci.db;

import com.github.mangila.fibonacci.PostgresTestContainerConfiguration;
import com.github.mangila.fibonacci.model.FibonacciOption;
import com.github.mangila.fibonacci.model.FibonacciResult;
import com.github.mangila.fibonacci.model.FibonacciResultEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Sql(scripts = {"classpath:schema.sql"}, config = @SqlConfig(separator = "^^"))
@Import({PostgresTestContainerConfiguration.class, FibonacciRepository.class})
class FibonacciRepositoryTest {

    @Autowired
    private FibonacciRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        var first = FibonacciResult.of(1, BigDecimal.ZERO);
        var second = FibonacciResult.of(2, BigDecimal.ONE);
        repository.insert(first);
        repository.insert(second);
        int rows = JdbcTestUtils.countRowsInTable(jdbcTemplate, "fibonacci_results");
        assertThat(rows).isEqualTo(2);
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "fibonacci_results");
    }

    @Test
    void queryById() {
        FibonacciResultEntity entity = repository.queryById(1);
        assertThat(entity).isNotNull();
        assertThat(entity.id()).isEqualTo(1);
        assertThat(entity.result()).isEqualTo(BigDecimal.ZERO);
        assertThat(entity.precision()).isEqualTo(1);
    }

    @Test
    void queryByIdFail() {
        assertThatThrownBy(() -> repository.queryById(100))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void queryForList() {
        var l = repository.queryForList(new FibonacciOption(0, 10));
        assertThat(l).isNotNull();
        assertThat(l).hasSize(2);
    }

    @Test
    void hasSequence() {
        assertThat(repository.hasSequence(1)).isTrue();
        assertThat(repository.hasSequence(2)).isTrue();
        assertThat(repository.hasSequence(3)).isFalse();
    }
}