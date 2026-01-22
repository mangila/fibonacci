package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.shared.FibonacciResult;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static com.github.mangila.fibonacci.FibonacciAlgorithm.ITERATIVE;
import static org.assertj.core.api.Assertions.assertThat;

class FibonacciComputeTaskTest {

    @Test
    void call() {
        int index = 10;
        FibonacciComputeTask task = new FibonacciComputeTask(ITERATIVE, index);
        var stopWatch = new StopWatch();
        stopWatch.start("FibonacciTask");
        FibonacciResult result = task.call();
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        System.out.println(result);
        assertThat(result).isNotNull();
        assertThat(result.sequence()).isEqualTo(index);
        assertThat(result.result()).isEqualTo(new BigDecimal("55"));
        assertThat(result.precision()).isEqualTo(2);
    }

}