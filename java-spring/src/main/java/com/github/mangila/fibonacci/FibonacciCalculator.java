package com.github.mangila.fibonacci;

import java.math.BigInteger;

public final class FibonacciCalculator {

    private FibonacciCalculator() {
    }

    /**
     * Naive recursive approach to calculate Fibonacci sequence.
     * Time Complexity: O(2^n)
     * Space Complexity: O(n)
     *
     * @param n The index of the Fibonacci number to calculate.
     * @return The n-th Fibonacci number.
     */
    public static BigInteger naiveRecursive(int n) {
        if (n <= 0) return BigInteger.ZERO;
        if (n == 1) return BigInteger.ONE;
        return naiveRecursive(n - 1).add(naiveRecursive(n - 2));
    }

    /**
     * Normal iterative approach to calculate Fibonacci sequence.
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     *
     * @param n The index of the Fibonacci number to calculate.
     * @return The n-th Fibonacci number.
     */
    public static BigInteger iterative(int n) {
        if (n <= 0) return BigInteger.ZERO;
        if (n == 1) return BigInteger.ONE;
        BigInteger a = BigInteger.ZERO;
        BigInteger b = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            BigInteger next = a.add(b);
            a = b;
            b = next;
        }
        return b;
    }

    /**
     * Fast doubling approach to calculate Fibonacci sequence.
     * F(2n) = Fn * (2*F(n+1) - Fn)
     * F(2n+1) = F(n+1)^2 + Fn^2
     * Time Complexity: O(log n)
     * Space Complexity: O(log n) due to recursion
     *
     * @param n The index of the Fibonacci number to calculate.
     * @return The n-th Fibonacci number.
     */
    public static BigInteger fastDoubling(int n) {
        return fastDoublingInternal(n)[0];
    }

    /**
     * Returns a pair (F(n), F(n+1)).
     */
    private static BigInteger[] fastDoublingInternal(int n) {
        if (n == 0) {
            return new BigInteger[]{BigInteger.ZERO, BigInteger.ONE};
        }
        BigInteger[] results = fastDoublingInternal(n >> 1);
        BigInteger fn = results[0];
        BigInteger fn1 = results[1];

        // c = F(2k) = F(k) * (2*F(k+1) - F(k))
        BigInteger c = fn.multiply(fn1.shiftLeft(1).subtract(fn));
        // d = F(2k+1) = F(k+1)^2 + F(k)^2
        BigInteger d = fn1.multiply(fn1).add(fn.multiply(fn));

        if ((n & 1) == 0) {
            return new BigInteger[]{c, d};
        } else {
            return new BigInteger[]{d, c.add(d)};
        }
    }
}
