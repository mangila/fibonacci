package com.github.mangila.fibonacci.scheduler.task;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.jobrunr.task.FibonacciComputeTask;
import com.github.mangila.fibonacci.jobrunr.model.FibonacciComputeResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FibonacciComputeTaskTest {

    @Test
    void call() {
        var task = new FibonacciComputeTask(FibonacciAlgorithm.ITERATIVE, 10);
        FibonacciComputeResult result = task.call();
        assertNotNull(result);
        assertEquals(new BigDecimal(55), result.result());
    }
}