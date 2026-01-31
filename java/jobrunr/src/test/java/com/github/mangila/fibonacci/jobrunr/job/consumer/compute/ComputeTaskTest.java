package com.github.mangila.fibonacci.jobrunr.job.consumer.compute;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.jobrunr.job.model.FibonacciComputeResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ComputeTaskTest {

    @Test
    void call() {
        var task = new ComputeTask(FibonacciAlgorithm.ITERATIVE, 10);
        FibonacciComputeResult result = task.call();
        assertNotNull(result);
        assertEquals(new BigDecimal(55), result.result());
    }
}