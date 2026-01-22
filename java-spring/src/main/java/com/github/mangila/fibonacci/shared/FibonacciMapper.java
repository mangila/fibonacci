package com.github.mangila.fibonacci.shared;

import com.github.mangila.fibonacci.web.dto.FibonacciDto;
import com.github.mangila.fibonacci.web.dto.FibonacciProjectionDto;
import com.github.mangila.fibonacci.db.model.FibonacciEntity;
import com.github.mangila.fibonacci.db.model.FibonacciProjection;
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
