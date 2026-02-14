package com.github.mangila.fibonacci.web.shared;

import io.github.mangila.ensure4j.Ensure;
import jakarta.validation.constraints.Positive;

public record FibonacciIdOption(@Positive int id) {

    public FibonacciIdOption {
        Ensure.positive(id);
    }
}
