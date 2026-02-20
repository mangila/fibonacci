package com.github.mangila.fibonacci.postgres;

import com.github.mangila.fibonacci.postgres.test.PostgresTestContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest(properties = {"spring.flyway.enabled=true"})
@PostgresTestContainer
@Import({PostgresRepository.class})
class PostgresRepositoryTest {

    @Autowired
    private PostgresRepository repository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "fibonacci_results", "fibonacci_metadata");
    }

    @DisplayName("Insert and verify double write")
    @Test
    void insertResult() {
        int sequenceId = 1;
        var optional = repository.insertResult(sequenceId, BigDecimal.ONE, 1);
        assertThat(optional).isPresent();
        var result = optional.get();
        assertThat(result.sequence()).isEqualTo(sequenceId);
        assertThat(result.result()).isEqualTo(BigDecimal.ONE);
        assertThat(result.precision()).isEqualTo(1);
        optional = repository.insertResult(sequenceId, BigDecimal.ONE, 1);
        assertThat(optional).isEmpty();
    }

    @Test
    void insertResultAndQuery() {
        int sequenceId = 1;
        repository.insertResult(sequenceId, BigDecimal.ONE, 1);
        var optional = repository.queryById(sequenceId);
        assertThat(optional).isPresent();
        var list = repository.queryProjectionList(50, 0);
        assertThat(list).hasSize(1);
    }

    @Test
    void batchInsertMetadata() {
        var metadata = List.of(
                FibonacciMetadataProjection.newInsert(1, "ITERATIVE"),
                FibonacciMetadataProjection.newInsert(2, "ITERATIVE")
        );
        repository.batchInsertMetadata(metadata);
        var rows = JdbcTestUtils.countRowsInTable(jdbcTemplate, "fibonacci_metadata");
        assertThat(rows).isEqualTo(2);
    }

    @Test
    void upsertMetadata() {
        var metadata = FibonacciMetadataProjection.newInsert(1, "ITERATIVE");
        repository.upsertMetadata(metadata);
        var rows = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "fibonacci_metadata", "scheduled = false");
        assertThat(rows).isEqualTo(1);
        metadata = FibonacciMetadataProjection.scheduled(1, "ITERATIVE");
        repository.upsertMetadata(metadata);
        rows = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "fibonacci_metadata", "scheduled = true");
        assertThat(rows).isEqualTo(1);
    }

    @Test
    void streamMetadataWhereScheduledFalseLocked() {
        // given
        var metadata = List.of(
                FibonacciMetadataProjection.scheduled(1, "ITERATIVE"),
                FibonacciMetadataProjection.newInsert(2, "ITERATIVE"),
                FibonacciMetadataProjection.newInsert(3, "ITERATIVE")
        );
        // when
        repository.batchInsertMetadata(metadata);
        var l = new ArrayList<FibonacciMetadataProjection>();
        repository.streamMetadataWhereScheduledFalseLocked(10, stream -> {
            stream.forEach(l::add);
        });
        // assert
        assertThat(l).hasSize(2);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void streamTestLocking() throws InterruptedException {
        // given
        var metadata = List.of(
                FibonacciMetadataProjection.scheduled(1, "ITERATIVE"),
                FibonacciMetadataProjection.newInsert(2, "ITERATIVE"),
                FibonacciMetadataProjection.newInsert(3, "ITERATIVE")
        );
        repository.batchInsertMetadata(metadata);

        // Acquires lock; streams metadata IDs; adds to list
        var acquiringListFuture = CompletableFuture.supplyAsync(() -> {
            var l = new ArrayList<FibonacciMetadataProjection>();
            repository.streamMetadataWhereScheduledFalseLocked(10, stream -> {
                stream.forEach(integer -> {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    l.add(integer);
                });
            });
            return l;
        }, Executors.newSingleThreadExecutor());

        // Wait here a while for the locking thread to spawn
        TimeUnit.MILLISECONDS.sleep(500);

        // Try to acquire lock on the main thread; should skip the first streams lock
        var shouldBeEmpty = new ArrayList<FibonacciMetadataProjection>();
        // Acquires new lock; should skip the first streams lock
        repository.streamMetadataWhereScheduledFalseLocked(10, stream -> {
            stream.forEach(shouldBeEmpty::add);
        });

        var acquiringList = acquiringListFuture.join();
        assertThat(acquiringList).hasSize(2);
        assertThat(shouldBeEmpty)
                .hasSize(0);
    }
}