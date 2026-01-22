package com.github.mangila.fibonacci.service;

import com.github.mangila.fibonacci.db.FibonacciRepository;
import com.github.mangila.fibonacci.shared.FibonacciMapper;
import com.github.mangila.fibonacci.shared.FibonacciResult;
import com.github.mangila.fibonacci.web.dto.FibonacciDto;
import com.github.mangila.fibonacci.web.dto.FibonacciQuery;
import com.github.mangila.fibonacci.web.dto.FibonacciProjectionDto;
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

    public void insert(FibonacciResult result) {
        Ensure.notNull(result);
        repository.insert(result);
    }

    public boolean hasSequence(int id) {
        Ensure.positive(id);
        return repository.hasSequence(id);
    }

}
