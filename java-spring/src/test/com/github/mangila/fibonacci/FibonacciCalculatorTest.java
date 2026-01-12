package com.github.mangila.fibonacci;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.util.StopWatch;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

class FibonacciCalculatorTest {

    @Test
    void test() {
        int index = 300_000;
        var stopWatch = new StopWatch();
        stopWatch.start("recursive");
      //  BigInteger r = FibonacciCalculator.naiveRecursive(index);
        stopWatch.stop();
        stopWatch.start("iterative");
        BigInteger i = FibonacciCalculator.iterative(index);
        stopWatch.stop();
        stopWatch.start("fastDoubling");
        BigInteger f = FibonacciCalculator.fastDoubling(index);
        assertThat(i).isEqualTo(f);
    }
}
