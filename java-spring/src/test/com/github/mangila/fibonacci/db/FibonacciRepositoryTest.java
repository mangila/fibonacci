package com.github.mangila.fibonacci.db;

import com.github.mangila.fibonacci.PostgresTestContainerConfiguration;
import com.github.mangila.fibonacci.model.FibonacciOption;
import com.github.mangila.fibonacci.model.FibonacciPair;
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
import java.util.ArrayList;

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
        var pair = FibonacciPair.DEFAULT;
        var l = new ArrayList<FibonacciResult>();
        l.add(pair.previous());
        l.add(pair.current());
        repository.batchInsert(l);
        int rows = JdbcTestUtils.countRowsInTable(jdbcTemplate, "fibonacci_results");
        assertThat(rows).isEqualTo(2);
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "fibonacci_results");
    }

    @Test
    void queryLatestPairOrDefault() {
        var data = new ArrayList<FibonacciResult>();
        var previous = FibonacciResult.of(3, BigDecimal.ONE);
        var current = FibonacciResult.of(4, BigDecimal.TEN);
        data.add(previous);
        data.add(current);
        repository.batchInsert(data);
        FibonacciPair pair = repository.queryLatestPairOrDefault();
        assertThat(pair).isNotNull();
        assertThat(pair.isDefault()).isFalse();
        var p = new FibonacciPair(previous, current);
        assertThat(p.equals(pair)).isTrue();
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
    void queryForOption() {
        var l = repository.queryForOption(new FibonacciOption(0, 10));
        assertThat(l).isNotNull();
        assertThat(l).hasSize(2);
    }
}