package com.github.mangila.fibonacci.postgres;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface PostgresRepository {

    Optional<FibonacciEntity> queryById(int id);

    Optional<FibonacciProjection> queryBySequence(int sequence);

    void streamMetadataLocked(int limit, Consumer<Stream<Integer>> consumer);

    Optional<FibonacciProjection> insert(int sequence, BigDecimal result, int precision);

    void upsertMetadata(int sequence, boolean sentToStream);
}
