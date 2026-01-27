package com.github.mangila.fibonacci.web.dto;

import io.github.mangila.ensure4j.Ensure;

public record FibonacciProjectionDto(int id,
                                     String sequence,
                                     int precision) {

    public FibonacciProjectionDto {
        Ensure.positive(id);
        Ensure.positive(precision);
        Ensure.notBlank(sequence);
    }
}
