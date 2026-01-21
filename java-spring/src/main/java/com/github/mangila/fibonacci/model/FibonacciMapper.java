package com.github.mangila.fibonacci.model;

import org.springframework.stereotype.Component;

@Component
public class FibonacciMapper {

    public FibonacciProjectionDto map(FibonacciProjection projection) {
        return new FibonacciProjectionDto(projection.id(), projection.sequence(), projection.precision());
    }

    public FibonacciDto map(FibonacciEntity entity) {
        return new FibonacciDto(entity.id(), entity.sequence(), entity.precision(), entity.result().toPlainString());
    }
}
