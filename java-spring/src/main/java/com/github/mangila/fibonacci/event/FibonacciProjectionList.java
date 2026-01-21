package com.github.mangila.fibonacci.event;

import com.github.mangila.fibonacci.model.FibonacciProjectionDto;

import java.util.List;

public record FibonacciProjectionList(List<FibonacciProjectionDto> value) {
}
