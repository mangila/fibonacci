package com.github.mangila.fibonacci.core.model;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import io.github.mangila.ensure4j.Ensure;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FibonacciComputeCommand(
        @NotNull FibonacciAlgorithm algorithm,
        @Min(1) int start,
        int end
) {
    public FibonacciComputeCommand {
        Ensure.isTrue(start <= end, "Start sequence must be smaller than end sequence");
        Ensure.isTrue(algorithm.isSuitable(start, end), "Algorithm is not suitable for given range");
    }
}
