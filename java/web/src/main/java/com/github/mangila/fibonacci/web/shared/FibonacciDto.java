package com.github.mangila.fibonacci.web.shared;

import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.ops.EnsureNumberOps;
import io.github.mangila.ensure4j.ops.EnsureStringOps;

public record FibonacciDto(int id, int sequence, String result, int precision) {

    private static final EnsureNumberOps ENSURE_NUMBER_OPS = Ensure.numbers();
    private static final EnsureStringOps ENSURE_STRING_OPS = Ensure.strings();

    public FibonacciDto {
        ENSURE_NUMBER_OPS.positive(id);
        ENSURE_NUMBER_OPS.positive(sequence);
        ENSURE_NUMBER_OPS.positive(precision);
        ENSURE_STRING_OPS.notBlank(result);
    }

}
