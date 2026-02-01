package com.github.mangila.fibonacci.jobrunr.job.consumer.compute;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.core.FibonacciCalculator;
import com.github.mangila.fibonacci.jobrunr.job.consumer.compute.model.FibonacciComputeResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComputeTaskTest {

    @Test
    void call() {
        var calculator = spy(FibonacciCalculator.getInstance());
        int sequence = 10;
        try (MockedStatic<FibonacciCalculator> mockedStatic = mockStatic(FibonacciCalculator.class)) {
            mockedStatic.when(FibonacciCalculator::getInstance).thenReturn(calculator);
            var task = new ComputeTask(FibonacciAlgorithm.RECURSIVE, sequence);
            FibonacciComputeResult result = task.call();
            assertNotNull(result);
            assertEquals(new BigDecimal(55), result.result());
            verify(calculator, atLeastOnce()).naiveRecursive(anyInt());
        }

        try (MockedStatic<FibonacciCalculator> mockedStatic = mockStatic(FibonacciCalculator.class)) {
            mockedStatic.when(FibonacciCalculator::getInstance).thenReturn(calculator);
            var task = new ComputeTask(FibonacciAlgorithm.ITERATIVE, sequence);
            FibonacciComputeResult result = task.call();
            assertNotNull(result);
            assertEquals(new BigDecimal(55), result.result());
            verify(calculator, times(1)).iterative(anyInt());
        }

        try (MockedStatic<FibonacciCalculator> mockedStatic = mockStatic(FibonacciCalculator.class)) {
            mockedStatic.when(FibonacciCalculator::getInstance).thenReturn(calculator);
            var task = new ComputeTask(FibonacciAlgorithm.FAST_DOUBLING, sequence);
            FibonacciComputeResult result = task.call();
            assertNotNull(result);
            assertEquals(new BigDecimal(55), result.result());
            verify(calculator, times(1)).fastDoubling(anyInt());
        }
    }
}