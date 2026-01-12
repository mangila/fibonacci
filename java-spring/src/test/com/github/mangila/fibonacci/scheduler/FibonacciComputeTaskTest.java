package com.github.mangila.fibonacci.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

import static com.github.mangila.fibonacci.FibonacciAlgorithm.ITERATIVE;

class FibonacciComputeTaskTest {

    @Test
    void call() {
        int index = 1000;
        FibonacciComputeTask task = new FibonacciComputeTask(ITERATIVE, index);

        var stopWatch = new StopWatch();
        stopWatch.start("FibonacciTask");
        task.call();
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
    }

}