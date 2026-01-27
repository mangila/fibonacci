package com.github.mangila.fibonacci.postgres;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface FibonacciRepository {

    Optional<FibonacciEntity> queryById(int id);

    void streamForList(int offset, int limit, Consumer<Stream<FibonacciProjection>> consumer);

    Optional<FibonacciProjection> insert(int sequence, BigDecimal result, int precision);

    void streamSequences(int max, Consumer<Stream<Integer>> consumer);

}
