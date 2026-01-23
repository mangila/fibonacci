package com.github.mangila.fibonacci.web.db;

import com.github.mangila.fibonacci.core.model.FibonacciEntity;
import com.github.mangila.fibonacci.core.model.FibonacciProjection;
import com.github.mangila.fibonacci.web.model.FibonacciQuery;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FibonacciRepository {

    private final JdbcTemplate jdbcTemplate;

    public FibonacciRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
}
