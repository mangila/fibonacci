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

    public Optional<FibonacciProjection> queryBySequence(int sequence) {
        ENSURE_NUMBER_OPS.positive(sequence);
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
    public void streamMetadataWhereComputedFalseLocked(int limit, Consumer<Stream<FibonacciMetadataProjection>> consumer) {
        ENSURE_NUMBER_OPS.positive(limit);
        Ensure.notNull(consumer);
        @Language("PostgreSQL") final String sql = """
                SELECT id, computed, algorithm
                FROM fibonacci_metadata
                WHERE computed = false
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

    public Optional<FibonacciProjection> insert(int sequence, BigDecimal result, int precision) {
        ENSURE_NUMBER_OPS.positive(sequence);
        Ensure.notNull(result);
        ENSURE_NUMBER_OPS.positive(precision);
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

    public void batchInsertMetadata(List<FibonacciMetadataProjection> metadataProjections) {
        ENSURE_COLLECTION_OPS.notEmpty(metadataProjections);
        @Language("PostgreSQL") final String sql = """
                INSERT INTO fibonacci_metadata
                (id,computed, updated_at)
                VALUES (:id, :computed, now())
                ON CONFLICT (id) DO NOTHING
                """;
        namedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(metadataProjections));
    }

    public void upsertMetadata(FibonacciMetadataProjection metadataProjection) {
        Ensure.notNull(metadataProjection);
        ENSURE_NUMBER_OPS.positive(metadataProjection.id());
        @Language("PostgreSQL") final String sql = """
                INSERT INTO fibonacci_metadata
                (id, computed)
                VALUES (:id,:computed)
                ON CONFLICT (id)
                DO UPDATE SET
                    computed = EXCLUDED.computed,
                    updated_at = now();
                """;
        jdbcClient.sql(sql)
                .param("id", metadataProjection.id())
                .param("computed", metadataProjection.computed())
                .update();
    }
}
