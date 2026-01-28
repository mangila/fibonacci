package com.github.mangila.fibonacci.web.dto;

import com.github.mangila.fibonacci.core.entity.FibonacciEntity;
import com.github.mangila.fibonacci.core.entity.FibonacciProjection;
import io.github.mangila.ensure4j.Ensure;
import org.springframework.stereotype.Component;

@Component
public class FibonacciDtoMapper {

    public FibonacciDto map(FibonacciEntity entity) {
        Ensure.notNull(entity);
        final String sequenceAsString = String.valueOf(entity.sequence());
        // This toPlainString operation can eat a lot of memory for large numbers
        final String resultAsString = entity.result().toPlainString();
        return new FibonacciDto(entity.id(),
                sequenceAsString,
                resultAsString,
                entity.precision());
    }

    public FibonacciProjectionDto map(FibonacciProjection projection) {
        Ensure.notNull(projection);
        final String sequenceAsString = String.valueOf(projection.sequence());
        return new FibonacciProjectionDto(projection.id(), sequenceAsString, projection.precision());
    }
}
