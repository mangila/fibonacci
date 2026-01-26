package com.github.mangila.fibonacci.scheduler.repository;

import com.github.mangila.fibonacci.scheduler.model.FibonacciResult;
import io.github.mangila.ensure4j.Ensure;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;
import java.util.stream.Stream;

@Repository
public class FibonacciRepository {

    private final JdbcClient client;

    public FibonacciRepository(JdbcClient client) {
        this.client = client;
    }

    public void insert(FibonacciResult fibonacciResult) {
        Ensure.notNull(fibonacciResult);
        // language=PostgreSQL
        final String sql = """
                INSERT INTO fibonacci_results
                (sequence, result, precision)
                VALUES (:sequence, :result, :precision)
                ON CONFLICT (sequence) DO NOTHING
                """;
        // ON CONFLICT (sequence) DO NOTHING - will ignore duplicate sequences
        // And will guard for some potential extra compute race conditions
        client.sql(sql)
                .param("sequence", fibonacciResult.sequence())
                .param("result", fibonacciResult.result())
                .param("precision", fibonacciResult.precision())
                .update();
    }

    @Transactional(readOnly = true)
    public void streamSequences(int max, Consumer<Stream<Integer>> consumer) {
        Ensure.positive(max);
        Ensure.notNull(consumer);
        final String sql = """
                SELECT sequence FROM fibonacci_results
                ORDER BY sequence
                LIMIT :max
                """;
        var stmt = client.sql(sql)
                .param("max", max)
                .withFetchSize(100)
                .query(Integer.class);
        try (var stream = stmt.stream()) {
            consumer.accept(stream);
        }
    }
}
