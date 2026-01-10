package com.github.mangila.fibonacci.db;

import com.github.mangila.fibonacci.scheduler.FibonacciCompute;
import io.github.mangila.ensure4j.Ensure;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class FibonacciRepository {

    private final JdbcTemplate jdbcTemplate;

    public FibonacciRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void batchInsert(List<FibonacciCompute> fibonacciComputes) {
        Ensure.notNull(fibonacciComputes);
        Ensure.notEmpty(fibonacciComputes);
        Ensure.notContainsNull(fibonacciComputes);
        // language=PostgreSQL
        final String sql = "INSERT INTO fibonacci_results (id, length, result) VALUES (?,?,?)";
        jdbcTemplate.batchUpdate(sql, fibonacciComputes, fibonacciComputes.size(), (ps, compute) -> {
            var bytes = compute.result().toByteArray();
            ps.setLong(1, compute.id());
            ps.setInt(2, bytes.length);
            ps.setBytes(3, bytes);
        });
    }

    public long nextOffset() {
        // language=PostgreSQL
        final String sql = "SELECT COALESCE(MAX(id), 1) FROM fibonacci_results";
        long max = Ensure.notNullOrElse(jdbcTemplate.queryForObject(sql, Long.class), 1L);
        if (max == 1) {
            return 1;
        }
        return max + 1;
    }
}
