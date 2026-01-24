package com.github.mangila.fibonacci.scheduler.task;

import com.github.mangila.fibonacci.scheduler.properties.FibonacciProperties;
import com.github.mangila.fibonacci.scheduler.repository.FibonacciRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

@Component
public class FibonacciEnqueueTask implements Callable<List<Integer>> {

    private static final Logger log = LoggerFactory.getLogger(FibonacciEnqueueTask.class);

    private final FibonacciRepository repository;
    private final FibonacciProperties properties;

    public FibonacciEnqueueTask(FibonacciRepository repository,
                                FibonacciProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    @Override
    public List<Integer> call() {
        final int offset = properties.getOffset();
        final int limit = properties.getLimit();
        log.info("Scheduling Fibonacci computations for sequences {}..{}",
                offset, limit);
        final List<Integer> requestedSequences = IntStream.range(offset, limit + 1)
                .boxed()
                .toList();
        final Set<Integer> existingSequences = new HashSet<>(repository.hasSequences(requestedSequences));
        return requestedSequences.stream()
                .filter(sequence -> !existingSequences.contains(sequence))
                .toList();
    }
}
