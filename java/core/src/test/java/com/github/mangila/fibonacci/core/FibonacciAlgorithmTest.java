package com.github.mangila.fibonacci.core;

import com.github.mangila.fibonacci.core.model.FibonacciCommand;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FibonacciAlgorithmTest {

    @Test
    void isSuitableRecursive() {
        var command = new FibonacciCommand(FibonacciAlgorithm.RECURSIVE, 9, 20, 100);
        assertThat(command.algorithm().isSuitable(command.offset(), command.limit())).isTrue();
        command = new FibonacciCommand(FibonacciAlgorithm.RECURSIVE, 10, 430, 100);
        assertThat(command.algorithm().isSuitable(command.offset(), command.limit())).isFalse();
    }

    @Test
    void isSuitableIterative() {
        var command = new FibonacciCommand(FibonacciAlgorithm.ITERATIVE, 9, 20, 100);
        assertThat(command.algorithm().isSuitable(command.offset(), command.limit())).isTrue();
        command = new FibonacciCommand(FibonacciAlgorithm.ITERATIVE, 10, 430, 100);
        assertThat(command.algorithm().isSuitable(command.offset(), command.limit())).isTrue();
        command = new FibonacciCommand(FibonacciAlgorithm.ITERATIVE, 100_000, 431, 100);
        assertThat(command.algorithm().isSuitable(command.offset(), command.limit())).isTrue();
        command = new FibonacciCommand(FibonacciAlgorithm.ITERATIVE, 200_000, 431, 100);
        assertThat(command.algorithm().isSuitable(command.offset(), command.limit())).isFalse();
    }
}