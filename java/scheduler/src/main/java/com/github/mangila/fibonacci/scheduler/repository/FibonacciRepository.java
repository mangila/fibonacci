package com.github.mangila.fibonacci.scheduler.repository;

import com.github.mangila.fibonacci.core.model.FibonacciResult;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FibonacciRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public FibonacciRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(FibonacciResult fibonacciResult) {
        // language=PostgreSQL
        String sql = """
                INSERT INTO fibonacci_results
                (sequence, result, precision)
                VALUES (:sequence, :result, :precision)
                ON CONFLICT (sequence) DO NOTHING
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("sequence", fibonacciResult.sequence());
        parameters.addValue("result", fibonacciResult.result());
        parameters.addValue("precision", fibonacciResult.precision());
        jdbcTemplate.update(sql, parameters);
    }

    public List<Integer> hasSequences(List<Integer> sequences) {
        if (sequences.isEmpty()) {
            return List.of();
        }
        // language=PostgreSQL
        final String sql = """
                SELECT sequence FROM fibonacci_results
                WHERE sequence IN(:sequences)
                ORDER BY sequence
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource("sequences", sequences);
        List<Integer> results = jdbcTemplate.queryForList(sql, parameters, Integer.class);
        return results;
    }
}
