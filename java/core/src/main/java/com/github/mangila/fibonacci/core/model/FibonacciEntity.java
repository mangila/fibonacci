package com.github.mangila.fibonacci.core.model;

import java.math.BigDecimal;

public record FibonacciEntity(int id,
                              int sequence,
                              BigDecimal result,
                              int precision) {
}
