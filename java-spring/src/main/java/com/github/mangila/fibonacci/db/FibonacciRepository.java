package com.github.mangila.fibonacci.db;

import com.github.mangila.fibonacci.model.FibonacciOption;
import com.github.mangila.fibonacci.model.FibonacciResult;
import com.github.mangila.fibonacci.model.FibonacciResultEntity;
import io.github.mangila.ensure4j.Ensure;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class FibonacciRepository {

    private final JdbcTemplate jdbcTemplate;

    public FibonacciRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(FibonacciResult fibonacciResult) {
        Ensure.notNull(fibonacciResult);
        // language=PostgreSQL
        final String sql = "INSERT INTO fibonacci_results (result, precision) VALUES (?,?)";
        final BigDecimal result = fibonacciResult.result();
        final int precision = result.precision();
        jdbcTemplate.update(sql, result, precision);
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
