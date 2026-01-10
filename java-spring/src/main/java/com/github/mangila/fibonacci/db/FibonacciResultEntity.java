package com.github.mangila.fibonacci.db;

import java.math.BigInteger;

public record FibonacciResultEntity(long id, int length, BigInteger result) {
}
