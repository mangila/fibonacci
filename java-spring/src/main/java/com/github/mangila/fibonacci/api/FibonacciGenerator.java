package com.github.mangila.fibonacci.api;

import com.github.mangila.fibonacci.model.FibonacciState;

import java.math.BigInteger;

public class FibonacciGenerator {

    public static FibonacciState generate(int index) {
        BigInteger previous = BigInteger.ZERO;
        BigInteger current = BigInteger.ONE;

        for (int i = 2; i <= index; i++) {
            BigInteger next = previous.add(current);
            previous = current;
            current = next;
        }
        return new FibonacciState(previous, current);
    }

}
