package com.github.mangila.fibonacci.postgres;

import com.github.mangila.fibonacci.postgres.test.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest(properties = {"spring.flyway.enabled=true"})
@PostgresTestContainer
@Import({DefaultFibonacciRepository.class})
class DefaultFibonacciRepositoryTest {

    @Autowired
    private DefaultFibonacciRepository repository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void queryById() {
    }

    @Test
    void streamForList() {
    }

    @Test
    void insert() {
        int sequenceId = 1;
        var optional = repository.insert(sequenceId, BigDecimal.ONE, 1);
        assertThat(optional).isPresent();
        optional = repository.insert(sequenceId, BigDecimal.ONE, 1);
        assertThat(optional).isEmpty();
    }

    @Test
    void streamSequences() {
    }
}