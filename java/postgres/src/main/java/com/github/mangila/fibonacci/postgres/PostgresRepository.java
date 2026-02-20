package com.github.mangila.fibonacci.postgres;

import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.ops.EnsureCollectionOps;
import io.github.mangila.ensure4j.ops.EnsureNumberOps;
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

    private static final EnsureNumberOps ENSURE_NUMBER_OPS = Ensure.numbers();
    private static final EnsureCollectionOps ENSURE_COLLECTION_OPS = Ensure.collections();
    // NamedParameterJdbcTemplate or JdbcTemplate must be used for batch operations
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcClient jdbcClient;

    public PostgresRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcClient jdbcClient) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcClient = jdbcClient;
    }

    public Optional<FibonacciEntity> queryById(int id) {
        ENSURE_NUMBER_OPS.positive(id);
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

    public List<FibonacciProjection> queryProjectionList(int limit, int offset) {
        ENSURE_NUMBER_OPS.positive(limit);
        ENSURE_NUMBER_OPS.positiveWithZero(offset);
        @Language("PostgreSQL") final String sql = """
                SELECT id, sequence, precision
                FROM fibonacci_results
                ORDER BY sequence
                LIMIT :limit
                OFFSET :offset;
                """;
        return jdbcClient.sql(sql)
                .param("limit", limit)
                .param("offset", offset)
                .query(FibonacciProjection.class)
                .list();
    }

    @Transactional
    public void streamMetadataWhereScheduledFalseLocked(int limit, Consumer<Stream<FibonacciMetadataProjection>> consumer) {
        ENSURE_NUMBER_OPS.positive(limit);
        Ensure.notNull(consumer);
        @Language("PostgreSQL") final String sql = """
                SELECT id, scheduled, computed, algorithm
                FROM fibonacci_metadata
                WHERE scheduled = false
                ORDER BY id
                LIMIT :limit
                FOR UPDATE SKIP LOCKED;
                """;
        var stmt = jdbcClient.sql(sql)
                .param("limit", limit)
                .withFetchSize(100)
                .query(FibonacciMetadataProjection.class);
        try (var stream = stmt.stream()) {
            consumer.accept(stream);
        }
    }

    public Optional<FibonacciEntity> insertResult(int sequence, BigDecimal result, int precision) {
        ENSURE_NUMBER_OPS.positive(sequence);
        Ensure.notNull(result);
        ENSURE_NUMBER_OPS.positive(precision);
        @Language("PostgreSQL") final String sql = """
                INSERT INTO fibonacci_results
                (sequence, result, precision)
                VALUES (:sequence, :result, :precision)
                ON CONFLICT (sequence) DO NOTHING
                RETURNING id, sequence, result, precision;
                """;
        return jdbcClient.sql(sql)
                .param("sequence", sequence)
                .param("result", result)
                .param("precision", precision)
                .query(FibonacciEntity.class)
                .optional();
    }

    public void batchInsertMetadata(List<FibonacciMetadataProjection> metadataProjections) {
        ENSURE_COLLECTION_OPS.notEmpty(metadataProjections);
        @Language("PostgreSQL") final String sql = """
                INSERT INTO fibonacci_metadata
                (id, scheduled, computed, algorithm, updated_at)
                VALUES (:id, :scheduled, :computed, :algorithm, now())
                ON CONFLICT (id) DO NOTHING
                """;
        namedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(metadataProjections));
    }

    public void upsertMetadata(FibonacciMetadataProjection metadataProjection) {
        Ensure.notNull(metadataProjection);
        ENSURE_NUMBER_OPS.positive(metadataProjection.id());
        @Language("PostgreSQL") final String sql = """
                INSERT INTO fibonacci_metadata
                (id, scheduled, computed, algorithm)
                VALUES (:id, :scheduled, :computed, :algorithm)
                ON CONFLICT (id)
                DO UPDATE SET
                    scheduled = EXCLUDED.scheduled,
                    computed = EXCLUDED.computed,
                    updated_at = now();
                """;
        jdbcClient.sql(sql)
                .param("id", metadataProjection.id())
                .param("scheduled", metadataProjection.scheduled())
                .param("computed", metadataProjection.computed())
                .param("algorithm", metadataProjection.algorithm())
                .update();
    }
}
