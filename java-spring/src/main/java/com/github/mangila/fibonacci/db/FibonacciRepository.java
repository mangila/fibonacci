package com.github.mangila.fibonacci.db;

import com.github.mangila.fibonacci.model.FibonacciOption;
import com.github.mangila.fibonacci.model.FibonacciPair;
import com.github.mangila.fibonacci.model.FibonacciResult;
import com.github.mangila.fibonacci.model.FibonacciResultEntity;
import io.github.mangila.ensure4j.Ensure;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class FibonacciRepository {

    private final JdbcTemplate jdbcTemplate;

    public FibonacciRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void batchInsert(List<FibonacciResult> fibonacciComputes) {
        Ensure.notNull(fibonacciComputes);
        Ensure.notEmpty(fibonacciComputes);
        Ensure.notContainsNull(fibonacciComputes);
        // language=PostgreSQL
        final String sql = "INSERT INTO fibonacci_results (result, precision) VALUES (?,?)";
        // Persists each computed Fibonacci result to database

        jdbcTemplate.batchUpdate(sql, fibonacciComputes, fibonacciComputes.size(), (ps, fibonacci) -> {
            final BigDecimal result = fibonacci.result();
            final int precision = result.precision();
            ps.setBigDecimal(1, result);
            ps.setInt(2, precision);
        });
    }

    public FibonacciPair queryLatestPairOrDefault() {
        // language=PostgreSQL
        final String sql = """
                SELECT id ,result FROM fibonacci_results
                ORDER BY id DESC
                LIMIT 2;
                """;
        var latestPair = jdbcTemplate.query(sql, (rs, _) ->
                new FibonacciResultEntity(
                        rs.getInt("id"),
                        rs.getBigDecimal("result"),
                        0));
        if (latestPair.isEmpty()) {
            return FibonacciPair.DEFAULT;
        }
        var previous = latestPair.get(1);
        var current = latestPair.getFirst();
        return new FibonacciPair(
                FibonacciResult.of(previous.id(), previous.result()),
                FibonacciResult.of(current.id(), current.result())
        );
    }

    public FibonacciResultEntity queryById(int id) {
        Ensure.min(1, id);
        // language=PostgreSQL
        final String sql = """
                SELECT id,result,precision FROM fibonacci_results
                WHERE id = ?
                """;
        return jdbcTemplate.queryForObject(sql,
                (rs, _) -> new FibonacciResultEntity(
                        rs.getInt("id"),
                        rs.getBigDecimal("result"),
                        rs.getInt("precision")
                ),
                id);
    }

    public List<FibonacciResultEntity> queryForList(FibonacciOption option) {
        Ensure.notNull(option);
        // language=PostgreSQL
        final String sql = """
                SELECT id, precision FROM fibonacci_results
                ORDER BY id
                OFFSET ?
                LIMIT ?;
                """;
        return jdbcTemplate.query(sql,
                (rs, _) -> new FibonacciResultEntity(
                        rs.getInt("id"),
                        null,
                        rs.getInt("precision")
                ),
                option.offset(), option.limit()
        );
    }
}
