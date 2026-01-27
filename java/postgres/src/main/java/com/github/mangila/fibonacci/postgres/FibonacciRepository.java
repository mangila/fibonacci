package com.github.mangila.fibonacci.postgres;

import io.github.mangila.ensure4j.Ensure;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Transactional(readOnly = true, timeout = 120)
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
                .withQueryTimeout(60)
                .query(FibonacciProjection.class);
        try (var stream = stmt.stream()) {
            consumer.accept(stream);
        }
    }

    public void insert(int sequence, BigDecimal result, int precision) {
        Ensure.positive(sequence);
        Ensure.notNull(result);
        Ensure.positive(precision);
        // language=PostgreSQL
        final String sql = """
                INSERT INTO fibonacci_results
                (sequence, result, precision)
                VALUES (:sequence, :result, :precision)
                ON CONFLICT (sequence) DO NOTHING
                """;
        // ON CONFLICT (sequence) DO NOTHING - will ignore duplicate sequences
        // And will guard for some potential extra compute race conditions
        jdbcClient.sql(sql)
                .param("sequence", sequence)
                .param("result", result)
                .param("precision", precision)
                .update();
    }

    @Transactional(readOnly = true)
    public void streamSequences(int max, Consumer<Stream<Integer>> consumer) {
        Ensure.positive(max);
        Ensure.notNull(consumer);
        final String sql = """
                SELECT sequence FROM fibonacci_results
                ORDER BY sequence
                LIMIT :max
                """;
        var stmt = jdbcClient.sql(sql)
                .param("max", max)
                .withFetchSize(100)
                .query(Integer.class);
        try (var stream = stmt.stream()) {
            consumer.accept(stream);
        }
    }
}
