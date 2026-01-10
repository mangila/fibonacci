package com.github.mangila.fibonacci.db;

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
    public void batchInsert(List<FibonacciResultEntity> results) {
        Ensure.notNull(results);
        Ensure.notEmpty(results);
        Ensure.notContainsNull(results);
        // language=PostgreSQL
        final String sql = "INSERT INTO fibonacci_results (id,result) VALUES (?,?)";
        jdbcTemplate.batchUpdate(sql, results, results.size(), (ps, result) -> {
            ps.setLong(1, result.id());
            ps.setBytes(2, result.result().toByteArray());
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
