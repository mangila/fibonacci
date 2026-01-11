package com.github.mangila.fibonacci.db;

import java.math.BigInteger;

public record FibonacciResultEntity(int id, int length, BigInteger result) {
}
