package com.github.mangila.fibonacci.web.dto;

import io.github.mangila.ensure4j.Ensure;

public record FibonacciDto(
        int id,
        String sequence,
        String result,
        int precision
) {
    public FibonacciDto {
        Ensure.positive(id);
        Ensure.notBlank(sequence);
        Ensure.notBlank(result);
        Ensure.positive(precision);
    }
}
