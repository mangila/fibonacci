package com.github.mangila.fibonacci.scheduler.repository;

import com.github.mangila.fibonacci.core.model.FibonacciResult;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FibonacciRepository {

    private final JdbcClient client;

    public FibonacciRepository(JdbcClient client) {
        this.client = client;
    }

    public void insert(FibonacciResult fibonacciResult) {
        // language=PostgreSQL
        final String sql = """
                INSERT INTO fibonacci_results
                (sequence, result, precision)
                VALUES (:sequence, :result, :precision)
                ON CONFLICT (sequence) DO NOTHING
                """;
        client.sql(sql)
                .param("sequence", fibonacciResult.sequence())
                .param("result", fibonacciResult.result())
                .param("precision", fibonacciResult.precision())
                .update();
    }
}
