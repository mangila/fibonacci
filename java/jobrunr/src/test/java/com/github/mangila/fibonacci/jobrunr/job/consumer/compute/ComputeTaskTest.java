package com.github.mangila.fibonacci.jobrunr.job.consumer.compute;

import com.github.mangila.fibonacci.jobrunr.job.consumer.compute.model.FibonacciComputeResult;
import com.github.mangila.fibonacci.shared.FibonacciAlgorithm;
import com.github.mangila.fibonacci.shared.FibonacciCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComputeTaskTest {

    private final int sequence = 10;

    @Test
    void callRecursive() {
        try (MockedStatic<FibonacciCalculator> mockedStatic = mockStatic(FibonacciCalculator.class, CALLS_REAL_METHODS)) {
            var task = new ComputeTask(FibonacciAlgorithm.RECURSIVE, sequence);
            FibonacciComputeResult computeResult = task.call();
            assertNotNull(computeResult);
            assertEquals(new BigDecimal(55), computeResult.result());
            mockedStatic.verify(() -> FibonacciCalculator.recursive(sequence), times(1));
        }
    }

    @Test
    void callIterative() {
        try (MockedStatic<FibonacciCalculator> mockedStatic = mockStatic(FibonacciCalculator.class, CALLS_REAL_METHODS)) {
            var task = new ComputeTask(FibonacciAlgorithm.ITERATIVE, sequence);
            FibonacciComputeResult computeResult = task.call();
            assertNotNull(computeResult);
            assertEquals(new BigDecimal(55), computeResult.result());
            mockedStatic.verify(() -> FibonacciCalculator.iterative(sequence), times(1));
        }
    }

    @Test
    void callFastDoubling() {
        try (MockedStatic<FibonacciCalculator> mockedStatic = mockStatic(FibonacciCalculator.class, CALLS_REAL_METHODS)) {
            var task = new ComputeTask(FibonacciAlgorithm.FAST_DOUBLING, sequence);
            FibonacciComputeResult computeResult = task.call();
            assertNotNull(computeResult);
            assertEquals(new BigDecimal(55), computeResult.result());
            mockedStatic.verify(() -> FibonacciCalculator.fastDoubling(sequence), times(1));
        }
    }
}