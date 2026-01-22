package com.github.mangila.fibonacci.db;

import com.github.mangila.fibonacci.db.model.FibonacciEntity;
import com.github.mangila.fibonacci.db.model.FibonacciProjection;
import com.github.mangila.fibonacci.shared.FibonacciResult;
import com.github.mangila.fibonacci.web.dto.FibonacciQuery;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class FibonacciRepository {

    private final JdbcTemplate jdbcTemplate;

    public FibonacciRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(FibonacciResult fibonacciResult) {
        // language=PostgreSQL
        final String sql = "INSERT INTO fibonacci_results (sequence, result, precision) VALUES (?,?,?)";
        final int sequence = fibonacciResult.sequence();
        final BigDecimal result = fibonacciResult.result();
        final int precision = result.precision();
        jdbcTemplate.update(sql, sequence, result, precision);
    }

    public Optional<FibonacciEntity> queryById(int id) {
        // language=PostgreSQL
        final String sql = """
                SELECT id, sequence, result, precision FROM fibonacci_results
                WHERE id = ?
                """;
        List<FibonacciEntity> entity = jdbcTemplate.query(sql,
                (rs, _) -> new FibonacciEntity(
                        rs.getInt("id"),
                        rs.getInt("sequence"),
                        rs.getBigDecimal("result"),
                        rs.getInt("precision")
                ),
                id);
        return entity.stream().findFirst();
    }

    public List<FibonacciProjection> queryForList(@NonNull FibonacciQuery query) {
        // language=PostgreSQL
        final String sql = """
                SELECT id, sequence, precision FROM fibonacci_results
                ORDER BY sequence
                OFFSET ?
                LIMIT ?;
                """;
        return jdbcTemplate.query(sql,
                (rs, _) -> new FibonacciProjection(
                        rs.getInt("id"),
                        rs.getInt("sequence"),
                        rs.getInt("precision")
                ),
                query.offset(), query.limit()
        );
    }

    public boolean hasSequence(int sequence) {
        // language=PostgreSQL
        final String sql = "SELECT 1 FROM fibonacci_results WHERE sequence = ? LIMIT 1";

        List<Integer> results = jdbcTemplate.queryForList(sql, Integer.class, sequence);

        return !results.isEmpty();
    }
}
