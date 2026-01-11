package com.github.mangila.fibonacci.model;

import java.math.BigDecimal;

public record FibonacciResultEntity(int id,
                                    BigDecimal result,
                                    int precision) {
}
