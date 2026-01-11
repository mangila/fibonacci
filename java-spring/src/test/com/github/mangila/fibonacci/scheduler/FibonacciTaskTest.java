package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.model.FibonacciOption;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class FibonacciTaskTest {

    @Test
    void call() {
        FibonacciOption option = new FibonacciOption(1, 1000);
        FibonacciTask task = new FibonacciTask(option);

        var stopWatch = new StopWatch();
        stopWatch.start("FibonacciTask");
        var fibs = task.call();
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.SECONDS));
        assertThat(fibs).hasSize(option.limit());
    }

}