package com.github.mangila.fibonacci.postgres;

import io.github.mangila.ensure4j.Ensure;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Repository
public class PostgresRepository {

    // NamedParameterJdbcTemplate must be used for batch operations
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcClient jdbcClient;

    public PostgresRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcClient jdbcClient) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcClient = jdbcClient;
    }

    public Optional<FibonacciEntity> queryById(int id) {
        Ensure.positive(id);
        @Language("PostgreSQL") final String sql = """
                SELECT id, sequence, result, precision
                FROM fibonacci_results
                WHERE id = :id
                """;
        return jdbcClient.sql(sql)
                .param("id", id)
                .query(FibonacciEntity.class)
                .optional();
    }

    public Optional<FibonacciProjection> queryBySequence(int sequence) {
        Ensure.positive(sequence);
        @Language("PostgreSQL") final String sql = """
                SELECT id, sequence, precision
                FROM fibonacci_results
                WHERE sequence = :sequence
                """;
        return jdbcClient.sql(sql)
                .param("sequence", sequence)
                .query(FibonacciProjection.class)
                .optional();
    }
    
    @Transactional
    public void streamMetadataWhereSentToZsetIsFalseLocked(int limit, Consumer<Stream<Integer>> consumer) {
        Ensure.positive(limit);
        Ensure.notNull(consumer);
        @Language("PostgreSQL") final String sql = """
                SELECT id
                FROM fibonacci_metadata
                WHERE sent_to_zset = false
                ORDER BY id
                LIMIT :limit
                FOR UPDATE SKIP LOCKED;
                """;
        var stmt = jdbcClient.sql(sql)
                .param("limit", limit)
                .withFetchSize(100)
                .query(Integer.class);
        try (var stream = stmt.stream()) {
            consumer.accept(stream);
        }
    }

    public Optional<FibonacciProjection> insert(int sequence, BigDecimal result, int precision) {
        Ensure.positive(sequence);
        Ensure.notNull(result);
        Ensure.positive(precision);
        @Language("PostgreSQL") final String sql = """
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

    public void batchUpsertMetadata(List<FibonacciMetadataProjection> metadataProjections) {
        Ensure.notEmpty(metadataProjections);
        @Language("PostgreSQL") final String sql = """
                INSERT INTO fibonacci_metadata
                (id,sent_to_zset,sent_to_stream)
                VALUES (:id, :sentToZset, :sentToStream)
                ON CONFLICT (id)
                DO UPDATE SET
                    sent_to_zset = EXCLUDED.sent_to_zset,
                    sent_to_stream = EXCLUDED.sent_to_stream,
                    updated_at = now();
                """;
        namedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(metadataProjections));
    }

    public void upsertMetadata(FibonacciMetadataProjection metadataProjection) {
        Ensure.notNull(metadataProjection);
        Ensure.positive(metadataProjection.id());
        @Language("PostgreSQL") final String sql = """
                INSERT INTO fibonacci_metadata
                (id, sent_to_zset, sent_to_stream)
                VALUES (:id,:sentToZset, :sentToStream)
                ON CONFLICT (id)
                DO UPDATE SET
                    sent_to_zset = EXCLUDED.sent_to_zset,
                    sent_to_stream = EXCLUDED.sent_to_stream,
                    updated_at = now();
                """;
        jdbcClient.sql(sql)
                .param("id", metadataProjection.id())
                .param("sentToZset", metadataProjection.sentToZset())
                .param("sentToStream", metadataProjection.sentToStream())
                .update();
    }
}
