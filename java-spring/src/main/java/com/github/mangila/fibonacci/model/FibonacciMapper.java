package com.github.mangila.fibonacci.model;

import org.springframework.stereotype.Component;

@Component
public class FibonacciMapper {

    public FibonacciDto map(FibonacciResultProjection projection) {
        return new FibonacciDto(projection.id(), projection.precision(), null);
    }

    public FibonacciDto map(FibonacciResultEntity entity) {
        return new FibonacciDto(entity.id(), entity.precision(), entity.result().toPlainString());
    }
}
