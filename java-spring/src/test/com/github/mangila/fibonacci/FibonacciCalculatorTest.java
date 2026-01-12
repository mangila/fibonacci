package com.github.mangila.fibonacci;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class FibonacciCalculatorTest {

    @Disabled
    @Test
    void test() {
        int index = 100_000;
        var stopWatch = new StopWatch();
        stopWatch.start("recursive");
        //BigInteger r = FibonacciCalculator.naiveRecursive(index);
        stopWatch.stop();
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
