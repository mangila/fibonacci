package com.github.mangila.fibonacci.web.service;

import com.github.mangila.fibonacci.web.db.FibonacciRepository;
import com.github.mangila.fibonacci.web.model.FibonacciDto;
import com.github.mangila.fibonacci.web.model.FibonacciMapper;
import com.github.mangila.fibonacci.web.model.FibonacciProjectionDto;
import com.github.mangila.fibonacci.core.model.FibonacciQuery;
import io.github.mangila.ensure4j.Ensure;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FibonacciService {

    private final FibonacciRepository repository;
    private final FibonacciMapper mapper;

    public FibonacciService(FibonacciRepository repository,
                            FibonacciMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public FibonacciDto queryById(int id) {
        Ensure.positive(id);
        return repository.queryById(id)
                .map(mapper::map)
                .orElseThrow();
    }

    public List<FibonacciProjectionDto> queryForList(FibonacciQuery query) {
        Ensure.notNull(query);
        return repository.queryForList(query)
                .stream()
                .map(mapper::map)
                .toList();
    }
}
