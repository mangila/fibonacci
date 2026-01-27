package com.github.mangila.fibonacci.postgres;

import com.github.mangila.fibonacci.postgres.test.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
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
    }

    @Test
    void streamSequences() {
    }
}