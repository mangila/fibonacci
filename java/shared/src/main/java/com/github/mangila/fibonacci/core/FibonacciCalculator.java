package com.github.mangila.fibonacci.core;

import java.math.BigInteger;

public final class FibonacciCalculator {

    private static final FibonacciCalculator INSTANCE = new FibonacciCalculator();

    private FibonacciCalculator() {
        // Private constructor to prevent instantiation
    }

    public static FibonacciCalculator getInstance() {
        return INSTANCE;
    }

    /**
     * Naive recursive approach to calculate Fibonacci sequence.
     *
     * @param n The sequence of the Fibonacci number to calculate.
     * @return The n-th Fibonacci number.
     */
    public BigInteger naiveRecursive(int n) {
        if (n <= 0) return BigInteger.ZERO;
        if (n == 1) return BigInteger.ONE;
        return naiveRecursive(n - 1).add(naiveRecursive(n - 2));
    }

    /**
     * Normal iterative approach to calculate Fibonacci sequence.
     *
     * @param n The sequence of the Fibonacci number to calculate.
     * @return The n-th Fibonacci number.
     */
    public BigInteger iterative(int n) {
        if (n <= 0) return BigInteger.ZERO;
        if (n == 1) return BigInteger.ONE;

        BigInteger previous2 = BigInteger.ZERO; // n - 2
        BigInteger previous1 = BigInteger.ONE;  // n - 1
        BigInteger current = BigInteger.ZERO;

        for (int i = 2; i <= n; i++) {
            current = previous1.add(previous2);
            previous2 = previous1;
            previous1 = current;
        }
        return current;
    }

    /**
     * Fast doubling approach to calculate Fibonacci sequence.
     * F(2n) = Fn * (2*F(n+1) - Fn)
     * F(2n+1) = F(n+1)^2 + Fn^2
     *
     * @param n The sequence of the Fibonacci number to calculate.
     * @return The n-th Fibonacci number.
     */
    public BigInteger fastDoubling(int n) {
        if (n == 0) return BigInteger.ZERO;
        if (n <= 2) return BigInteger.ONE;

        BigInteger a = BigInteger.ZERO; // F(k)
        BigInteger b = BigInteger.ONE;  // F(k+1)

        // Find the highest bit set to 1. For n=1,000,000, this is the 19th bit.
        for (int i = Integer.SIZE - Integer.numberOfLeadingZeros(n) - 1; i >= 0; i--) {
            // Standard Fast Doubling Identities:
            // c = F(2k) = F(k) * [2*F(k+1) - F(k)]
            BigInteger c = a.multiply(b.shiftLeft(1).subtract(a));
            // d = F(2k+1) = F(k+1)^2 + F(k)^2
            BigInteger d = a.multiply(a).add(b.multiply(b));

            if (((n >> i) & 1) != 0) {
                // If the current bit is 1: we move from k to 2k+1
                // New F(k) = d, New F(k+1) = c + d
                a = d;
                b = c.add(d);
            } else {
                // If the current bit is 0: we move from k to 2k
                // New F(k) = c, New F(k+1) = d
                a = c;
                b = d;
            }
        }
        return a;
    }
}