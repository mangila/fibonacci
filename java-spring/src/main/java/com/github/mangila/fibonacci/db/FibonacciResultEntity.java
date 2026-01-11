package com.github.mangila.fibonacci.db;

import java.math.BigDecimal;

public record FibonacciResultEntity(int id,
                                    BigDecimal result,
                                    int precision) {
}
