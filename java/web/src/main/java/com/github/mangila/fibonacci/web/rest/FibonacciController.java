package com.github.mangila.fibonacci.web.rest;

import com.github.mangila.fibonacci.postgres.FibonacciEntity;
import com.github.mangila.fibonacci.postgres.FibonacciProjection;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.web.shared.FibonacciDto;
import com.github.mangila.fibonacci.web.shared.FibonacciMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FibonacciController {

    private static final Logger log = LoggerFactory.getLogger(FibonacciController.class);

    private final FibonacciMapper mapper;
    private final PostgresRepository repository;

    public FibonacciController(FibonacciMapper mapper,
                               PostgresRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @GetMapping("list")
    public ResponseEntity<List<FibonacciProjection>> queryList(@RequestParam int limit,
                                       @RequestParam int offset) {
        log.info("Querying list of fibonacci numbers: limit={}, offset={}", limit, offset);
        List<FibonacciProjection> projections = repository.queryList(limit, offset);
        return ResponseEntity.ok(projections);
    }

    @GetMapping("id")
    public ResponseEntity<FibonacciDto> queryById(@RequestParam int id) {
        log.info("Querying fibonacci number by id: {}", id);
        FibonacciEntity entity = repository.queryById(id).orElseThrow();
        FibonacciDto dto = mapper.toDto(entity);
        return ResponseEntity.ok(dto);
    }

}
