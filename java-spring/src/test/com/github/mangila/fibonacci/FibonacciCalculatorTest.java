package com.github.mangila.fibonacci;

import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.math.BigInteger;
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

    @Test
    void HUNDRED() {
        int index = HUNDRED;
        var stopWatch = new StopWatch();
        stopWatch.start("iterative");
        BigInteger i = FibonacciCalculator.iterative(index);
        stopWatch.stop();
        stopWatch.start("fastDoubling");
        BigInteger f = FibonacciCalculator.fastDoubling(index);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        assertThat(i).isEqualTo(f);
    }

    @Test
    void THOUSAND() {
        int index = THOUSAND;
        var stopWatch = new StopWatch();
        stopWatch.start("iterative");
        BigInteger i = FibonacciCalculator.iterative(index);
        stopWatch.stop();
        stopWatch.start("fastDoubling");
        BigInteger f = FibonacciCalculator.fastDoubling(index);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        assertThat(i).isEqualTo(f);
    }

    @Test
    void TEN_THOUSAND() {
        int index = TEN_THOUSAND;
        var stopWatch = new StopWatch();
        stopWatch.start("iterative");
        BigInteger i = FibonacciCalculator.iterative(index);
        stopWatch.stop();
        stopWatch.start("fastDoubling");
        BigInteger f = FibonacciCalculator.fastDoubling(index);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        assertThat(i).isEqualTo(f);
    }

    @Test
    void ONE_HUNDRED_THOUSAND() {
        int index = ONE_HUNDRED_THOUSAND;
        var stopWatch = new StopWatch();
        stopWatch.start("iterative");
        BigInteger i = FibonacciCalculator.iterative(index);
        stopWatch.stop();
        stopWatch.start("fastDoubling");
        BigInteger f = FibonacciCalculator.fastDoubling(index);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        assertThat(i).isEqualTo(f);
    }

    @Test
    void FIVE_HUNDRED_THOUSAND() {
        int index = FIVE_HUNDRED_THOUSAND;
        var stopWatch = new StopWatch();
        stopWatch.start("iterative");
        BigInteger i = FibonacciCalculator.iterative(index);
        stopWatch.stop();
        stopWatch.start("fastDoubling");
        BigInteger f = FibonacciCalculator.fastDoubling(index);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        assertThat(i).isEqualTo(f);
    }

    @Test
    void ONE_MILLION() {
        int index = ONE_MILLION;
        var stopWatch = new StopWatch();
        stopWatch.start("iterative");
        BigInteger i = FibonacciCalculator.iterative(index);
        stopWatch.stop();
        stopWatch.start("fastDoubling");
        BigInteger f = FibonacciCalculator.fastDoubling(index);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        assertThat(i).isEqualTo(f);
    }
}
