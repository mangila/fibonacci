package com.github.mangila.fibonacci.db.model;

import java.math.BigDecimal;

public record FibonacciEntity(int id,
                              int sequence,
                              BigDecimal result,
                              int precision) {
}
