package com.github.mangila.fibonacci.postgres;

import io.github.mangila.ensure4j.Ensure;

public record FibonacciProjection(int id,
                                  int sequence,
                                  int precision) {

    public FibonacciProjection {
        Ensure.positive(id);
        Ensure.positive(sequence);
        Ensure.positive(precision);
    }
}
