package com.github.mangila.fibonacci.postgres;

import io.github.mangila.ensure4j.Ensure;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

// Here is a concrete implementation of the FibonacciRepository
// an application module like this can also share an interface or just be a packaging pom
@Repository
public class DefaultPostgresRepository implements PostgresRepository {

    private final JdbcClient jdbcClient;

    public DefaultPostgresRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
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

    @Override
    public Optional<FibonacciProjection> queryBySequence(int sequence) {
        Ensure.positive(sequence);
        // language=PostgreSQL
        final String sql = """
                SELECT id, sequence, precision
                FROM fibonacci_results
                WHERE sequence = :sequence
                """;
        return jdbcClient.sql(sql)
                .param("sequence", sequence)
                .query(FibonacciProjection.class)
                .optional();
    }

    @Override
    public void streamMetadataLocked(int limit, Consumer<Stream<Integer>> consumer) {
        Ensure.positive(limit);
        Ensure.notNull(consumer);
        // language=PostgreSQL
        final String sql = """
                SELECT id
                FROM fibonacci_metadata
                WHERE sent_to_stream = false
                ORDER BY id
                LIMIT :limit
                FOR UPDATE SKIP LOCKED;
                """;
        //  FOR UPDATE SKIP LOCKED is a Postgres specific feature that locks the rows,
        //  if another "worker" is trying to lock the same rows,
        //  it will skip them and move to the next row.
        var stmt = jdbcClient.sql(sql)
                .param("limit", limit)
                .withFetchSize(100)
                .query(Integer.class);
        try (var stream = stmt.stream()) {
            consumer.accept(stream);
        }
    }

    @Override
    public Optional<FibonacciProjection> insert(int sequence, BigDecimal result, int precision) {
        Ensure.positive(sequence);
        Ensure.notNull(result);
        Ensure.positive(precision);
        // language=PostgreSQL
        final String sql = """
                INSERT INTO fibonacci_results
                (sequence, result, precision)
                VALUES (:sequence, :result, :precision)
                ON CONFLICT (sequence) DO NOTHING
                RETURNING id, sequence, precision;
                """;
        return jdbcClient.sql(sql)
                .param("sequence", sequence)
                .param("result", result)
                .param("precision", precision)
                .query(FibonacciProjection.class)
                .optional();
    }

    @Override
    public void upsertMetadata(int sequence, boolean sentToStream) {
        Ensure.positive(sequence);
        final String sql = """
                INSERT INTO fibonacci_metadata
                (id, sent_to_stream)
                VALUES (:sequence, :sentToStream)
                ON CONFLICT (id)
                DO UPDATE SET
                    sent_to_stream = EXCLUDED.sent_to_stream,
                    updated_at = now();
                """;
        jdbcClient.sql(sql)
                .param("sequence", sequence)
                .param("sentToStream", sentToStream)
                .update();
    }
}
