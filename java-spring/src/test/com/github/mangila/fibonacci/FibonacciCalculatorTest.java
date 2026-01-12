package com.github.mangila.fibonacci;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.util.StopWatch;

import java.math.BigInteger;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FibonacciCalculatorTest {

    private static final int TEN = 10;
    private static final int HUNDRED = 100;
    private static final int THOUSAND = 1_000;
    private static final int TEN_THOUSAND = 10_000;
    private static final int ONE_HUNDRED_THOUSAND = 100_000;
    private static final int FIVE_HUNDRED_THOUSAND = 500_000;
    private static final int ONE_MILLION = 1_000_000;

    @TestFactory
    Collection<DynamicTest> fibonacciNumbers() {
        return Stream.of(HUNDRED, THOUSAND, TEN_THOUSAND, ONE_HUNDRED_THOUSAND, FIVE_HUNDRED_THOUSAND, ONE_MILLION)
                .map(testInt -> DynamicTest.dynamicTest("Fibonacci number " + testInt, () -> {
                    var stopWatch = new StopWatch();
                    stopWatch.start("iterative");
                    BigInteger i = FibonacciCalculator.iterative(testInt);
                    stopWatch.stop();
                    stopWatch.start("fastDoubling");
                    BigInteger f = FibonacciCalculator.fastDoubling(testInt);
                    stopWatch.stop();
                    System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
                    assertThat(i).isEqualTo(f);
                })).toList();
    }

    @Test
    void TEN() {
        int index = TEN;
        var stopWatch = new StopWatch();
        stopWatch.start("recursive");
        BigInteger r = FibonacciCalculator.naiveRecursive(index);
        stopWatch.stop();
        stopWatch.start("iterative");
        BigInteger i = FibonacciCalculator.iterative(index);
        stopWatch.stop();
        stopWatch.start("fastDoubling");
        BigInteger f = FibonacciCalculator.fastDoubling(index);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        assertThat(r)
                .isEqualTo(i)
                .isEqualTo(f);
    }
}
