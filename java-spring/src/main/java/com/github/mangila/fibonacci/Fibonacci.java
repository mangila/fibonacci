package com.github.mangila.fibonacci;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.mangila.fibonacci.model.FibonacciState;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

public class Fibonacci {

    public static final Cache<Long, BigInteger> FIBONACCI_NEXT_CACHE = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    public static FibonacciState generate(long index) {
        BigInteger previous = BigInteger.ZERO;
        BigInteger current = BigInteger.ONE;

        for (long i = 2; i <= index; i++) {
            BigInteger next = FIBONACCI_NEXT_CACHE.getIfPresent(i);
            if (next == null) {
                next = previous.add(current);
                FIBONACCI_NEXT_CACHE.put(i, next);
            }
            previous = current;
            current = next;
        }
        return new FibonacciState(previous, current);
    }
}
