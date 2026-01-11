package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.config.FibonacciComputeTaskConfig;
import com.github.mangila.fibonacci.model.FibonacciPair;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class FibonacciComputeTaskTest {

    @Test
    void call() {
        int limit = 100_000;
        FibonacciComputeTaskConfig config = new FibonacciComputeTaskConfig(FibonacciPair.DEFAULT, limit);
        FibonacciComputeTask task = new FibonacciComputeTask(config);

        var stopWatch = new StopWatch();
        stopWatch.start("FibonacciTask");
        var fibs = task.call();
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.SECONDS));
        assertThat(fibs).hasSize(limit);
    }

}