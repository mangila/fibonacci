package com.github.mangila.fibonacci.postgres;

import com.github.mangila.fibonacci.postgres.test.PostgresTestContainer;
import org.junit.jupiter.api.AfterEach;
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

    @Test
    void queryById() {
        int sequenceId = 1;
        repository.insert(sequenceId, BigDecimal.ONE, 1);
        // assert
        assertThat(repository.queryById(sequenceId)).isPresent();
    }

    @Test
    void queryBySequence() {
        int sequenceId = 10;
        repository.insert(sequenceId, BigDecimal.ONE, 1);
        // assert
        assertThat(repository.queryBySequence(sequenceId)).isPresent();
    }

    @Test
    void insert() {
        int sequenceId = 1;
        var optional = repository.insert(sequenceId, BigDecimal.ONE, 1);
        assertThat(optional).isPresent();
        optional = repository.insert(sequenceId, BigDecimal.ONE, 1);
        // assert
        assertThat(optional).isEmpty();
    }

    @Test
    void streamMetadataWhereSentToZsetIsFalseLocked() {
        int sequenceId = 1;
        repository.insert(sequenceId, BigDecimal.ONE, 1);
        repository.insert(sequenceId + 1, BigDecimal.ONE, 1);
        repository.upsertMetadata(new FibonacciMetadataProjection(sequenceId + 1, false, false));
        repository.upsertMetadata(new FibonacciMetadataProjection(sequenceId, true, true));
        var l = new ArrayList<Integer>();
        repository.streamMetadataWhereSentToZsetIsFalseLocked(10, stream -> {
            stream.forEach(l::add);
        });
        assertThat(l).hasSize(1);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void streamMetadataIdWhereNotSentToStreamLocked() throws InterruptedException {
        int sequenceId = 1;
        repository.insert(sequenceId, BigDecimal.ONE, 1);
        repository.insert(sequenceId + 1, BigDecimal.ONE, 1);
        repository.upsertMetadata(new FibonacciMetadataProjection(sequenceId + 1, false, false));
        repository.upsertMetadata(new FibonacciMetadataProjection(sequenceId, true, true));

        // Acquires lock; streams metadata IDs; adds to list
        var lockingThread = CompletableFuture.supplyAsync(() -> {
            var l = new ArrayList<Integer>();
            repository.streamMetadataWhereSentToZsetIsFalseLocked(10, stream -> {
                stream.forEach(integer -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
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

        var skippedLockThread = new ArrayList<Integer>();
        // Acquires new lock; should skip the first streams lock
        repository.streamMetadataWhereSentToZsetIsFalseLocked(10, stream -> {
            stream.forEach(skippedLockThread::add);
        });

        var lockingThreadList = lockingThread.join();
        assertThat(lockingThreadList).hasSize(2)
                .containsExactlyInAnyOrder(1, 2);
        assertThat(skippedLockThread)
                .hasSize(0);
    }
}