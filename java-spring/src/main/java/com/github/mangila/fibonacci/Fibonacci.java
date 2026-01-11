package com.github.mangila.fibonacci;

import com.github.mangila.fibonacci.model.FibonacciState;

import java.math.BigInteger;

public class Fibonacci {

    /**
     * Generates cached Fibonacci state for a given index
     */
    public static FibonacciState generate(int index) {
        BigInteger previous = BigInteger.ZERO;
        BigInteger current = BigInteger.ONE;

        if (index == 1) {
            return new FibonacciState(previous, current);
        }

        for (int i = 2; i <= index; i++) {
            BigInteger next = previous.add(current);
            previous = current;
            current = next;
        }
        return new FibonacciState(previous, current);
    }
}
