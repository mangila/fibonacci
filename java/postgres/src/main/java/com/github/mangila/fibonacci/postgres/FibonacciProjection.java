package com.github.mangila.fibonacci.postgres;

import io.github.mangila.ensure4j.Ensure;

import java.util.Map;

public record FibonacciProjection(int id,
                                  int sequence,
                                  int precision) {

    public FibonacciProjection {
        Ensure.positive(id);
        Ensure.positive(sequence);
        Ensure.positive(precision);
    }

    public Map<String, String> asStringMap() {
        String id = String.valueOf(this.id);
        String sequence = String.valueOf(this.sequence);
        String precision = String.valueOf(this.precision);
        return Map.of("id", id, "sequence", sequence, "precision", precision);
    }
}
