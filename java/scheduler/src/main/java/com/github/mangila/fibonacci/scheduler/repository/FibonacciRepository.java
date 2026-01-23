package com.github.mangila.fibonacci.scheduler.repository;

import com.github.mangila.fibonacci.core.model.FibonacciResult;
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

    public void save(FibonacciResult fibonacciResult) {
        final int sequence = fibonacciResult.sequence();
        final BigDecimal result = fibonacciResult.result();
        final int precision = fibonacciResult.precision();
        // language=PostgreSQL
        String sql = """
                INSERT INTO fibonacci_results
                (sequence, result, precision)
                VALUES (?, ?,?)
                """;
        jdbcTemplate.update(sql,
                sequence, result, precision);
    }

    public boolean hasSequence(int sequence) {
        // language=PostgreSQL
        final String sql = """
                SELECT 1 FROM fibonacci_results
                WHERE sequence = ?
                LIMIT 1
                """;
        List<Integer> results = jdbcTemplate.queryForList(sql, Integer.class, sequence);
        return !results.isEmpty();
    }
}
