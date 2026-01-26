package com.github.mangila.fibonacci.core;

import io.github.mangila.ensure4j.EnsureException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FibonacciAlgorithmTest {

    @Test
    void isSuitableRecursive() {
        var algorithm = FibonacciAlgorithm.RECURSIVE;
        assertThat(algorithm.isSuitable(10)).isTrue();
        assertThat(algorithm.isSuitable(45)).isFalse();
        assertThat(algorithm.isSuitable(30)).isTrue();
    }

    @Test
    void isSuitableIterative() {
        var algorithm = FibonacciAlgorithm.ITERATIVE;
        assertThat(algorithm.isSuitable(10)).isTrue();
        assertThat(algorithm.isSuitable(100_000)).isTrue();
        assertThat(algorithm.isSuitable(200_000)).isFalse();
    }

    @Test
    void isSuitableThrowsException() {
        var algorithm = FibonacciAlgorithm.RECURSIVE;
        assertThatThrownBy(() -> algorithm.isSuitable(-1))
                .isInstanceOf(EnsureException.class);
    }
}