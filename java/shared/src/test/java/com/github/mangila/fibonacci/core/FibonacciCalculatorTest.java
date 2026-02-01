package com.github.mangila.fibonacci.core;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class FibonacciCalculatorTest {

    private static final int TEN = 10;
    private static final int HUNDRED = 100;
    private static final int THOUSAND = 1_000;
    private static final int TEN_THOUSAND = 10_000;
    private static final int ONE_HUNDRED_THOUSAND = 100_000;
    private static final int FIVE_HUNDRED_THOUSAND = 500_000;
    private static final int ONE_MILLION = 1_000_000;
    private static final int TEN_MILLION = 10_000_000;

    private final FibonacciCalculator calculator = FibonacciCalculator.getInstance();

    @DisplayName("Fibonacci calculation with different algos should be the same")
    @Test
    void test() {
        int n = 35;
        var recursiveResult = calculator.naiveRecursive(n);
        var iterativeResult = calculator.iterative(n);
        var fastDoublingResult = calculator.fastDoubling(n);
        assertThat(recursiveResult)
                .isEqualTo(iterativeResult)
                .isEqualTo(fastDoublingResult);
    }

    @Disabled
    @DisplayName("Fibonacci calculation with fast doubling algorithm")
    @ParameterizedTest
    @ValueSource(ints = {
            TEN, HUNDRED, THOUSAND, TEN_THOUSAND, ONE_HUNDRED_THOUSAND, FIVE_HUNDRED_THOUSAND, ONE_MILLION, TEN_MILLION})
    void fastDoubling(int n) {
        var stopWatch = new StopWatch();
        stopWatch.start();
        calculator.fastDoubling(n);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
    }

    @Disabled
    @DisplayName("Fibonacci calculation with iterative algorithm")
    @ParameterizedTest
    @ValueSource(ints = {TEN, HUNDRED, THOUSAND, TEN_THOUSAND, ONE_HUNDRED_THOUSAND, FIVE_HUNDRED_THOUSAND, ONE_MILLION})
    void iterative(int n) {
        var stopWatch = new StopWatch();
        stopWatch.start();
        calculator.iterative(n);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
    }

}