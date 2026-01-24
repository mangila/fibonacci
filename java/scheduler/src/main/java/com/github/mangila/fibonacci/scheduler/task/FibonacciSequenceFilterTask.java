package com.github.mangila.fibonacci.scheduler.task;

import com.github.mangila.fibonacci.scheduler.repository.FibonacciRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

public class FibonacciSequenceFilterTask implements Callable<List<Integer>> {

    private static final Logger log = LoggerFactory.getLogger(FibonacciSequenceFilterTask.class);

    private final FibonacciRepository repository;
    private final int offset;
    private final int limit;

    public FibonacciSequenceFilterTask(FibonacciRepository repository,
                                       int offset,
                                       int limit) {
        this.repository = repository;
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    public List<Integer> call() {
        log.info("Scheduling Fibonacci computations for sequences {}..{}", offset, limit);
        final List<Integer> requestedSequences = IntStream.range(offset, limit + 1)
                .boxed()
                .toList();
        final Set<Integer> existingSequences = new HashSet<>(repository.hasSequences(requestedSequences));
        return requestedSequences.stream()
                .filter(sequence -> !existingSequences.contains(sequence))
                .toList();
    }
}
