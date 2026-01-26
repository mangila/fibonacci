package com.github.mangila.fibonacci.web.repository;

import com.github.mangila.fibonacci.core.entity.FibonacciEntity;
import com.github.mangila.fibonacci.core.entity.FibonacciProjection;
import io.github.mangila.ensure4j.Ensure;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Repository
public class FibonacciRepository {

    private final JdbcClient jdbcClient;

    public FibonacciRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<FibonacciEntity> queryById(int id) {
        Ensure.positive(id);
        // language=PostgreSQL
        final String sql = """
                SELECT id, sequence, result, precision
                FROM fibonacci_results
                WHERE id = :id
                """;
        return jdbcClient.sql(sql)
                .param("id", id)
                .query(FibonacciEntity.class)
                .optional();
    }

    @Transactional(readOnly = true)
    public void streamForList(int offset, int limit, Consumer<Stream<FibonacciProjection>> consumer) {
        Ensure.positive(offset);
        Ensure.positive(limit);
        Ensure.notNull(consumer);
        // language=PostgreSQL
        final String sql = """
                SELECT id, sequence, precision
                FROM fibonacci_results
                ORDER BY sequence
                OFFSET :offset
                LIMIT :limit;
                """;
        var stmt = jdbcClient.sql(sql)
                .param("offset", offset)
                .param("limit", limit)
                .withFetchSize(100)
                .query(FibonacciProjection.class);
        try (var stream = stmt.stream()) {
            consumer.accept(stream);
        }
    }
}
