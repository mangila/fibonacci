package com.github.mangila.fibonacci.web.service;

import com.github.mangila.fibonacci.core.model.FibonacciEntity;
import com.github.mangila.fibonacci.core.model.FibonacciProjection;
import com.github.mangila.fibonacci.core.model.FibonacciQuery;
import com.github.mangila.fibonacci.web.repository.FibonacciRepository;
import io.github.mangila.ensure4j.Ensure;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
public class FibonacciService {

    private final FibonacciRepository repository;

    public FibonacciService(FibonacciRepository repository) {
        this.repository = repository;
    }

    public FibonacciEntity queryById(int id) {
        Ensure.positive(id);
        return repository.queryById(id).orElseThrow();
    }

    public void streamForList(FibonacciQuery query, Consumer<Stream<FibonacciProjection>> consumer) {
        Ensure.notNull(query);
        repository.streamForList(query, consumer);
    }
}
