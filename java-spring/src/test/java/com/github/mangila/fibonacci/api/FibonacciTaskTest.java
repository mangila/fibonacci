package com.github.mangila.fibonacci.api;

import com.github.mangila.fibonacci.model.FibonacciOption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FibonacciTaskTest {

    @Test
    void run() {
        FibonacciOption option = new FibonacciOption(1, 10);
        FibonacciTask task = new FibonacciTask(option);
        task.run();
    }
}