package com.github.mangila.fibonacci.scheduler.model;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import io.github.mangila.ensure4j.Ensure;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FibonacciComputeCommand(
        @NotNull FibonacciAlgorithm algorithm,
        @Positive int start,
        @Positive int end
) {
    public FibonacciComputeCommand {
        Ensure.isTrue(start <= end, "Start sequence must be smaller than end sequence");
        Ensure.isTrue(algorithm.isSuitable(end), "Algorithm is not suitable for given range");
    }
}
