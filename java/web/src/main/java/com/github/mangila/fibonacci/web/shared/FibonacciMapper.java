package com.github.mangila.fibonacci.web.shared;

import com.github.mangila.fibonacci.postgres.FibonacciEntity;
import org.springframework.stereotype.Component;

@Component
public class FibonacciMapper {

    public FibonacciDto toDto(FibonacciEntity entity) {
        // This operation can eat memory on huge indices
        String resultAsString = entity.result().toPlainString();
        return new FibonacciDto(entity.id(),
                entity.sequence(),
                resultAsString,
                entity.precision());
    }
}
