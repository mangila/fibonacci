package com.github.mangila.fibonacci.scheduler.scheduler;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.core.model.FibonacciResult;
import com.github.mangila.fibonacci.scheduler.repository.FibonacciRepository;
import com.github.mangila.fibonacci.scheduler.task.FibonacciComputeTask;
import com.github.mangila.fibonacci.scheduler.task.FibonacciSequenceFilterTask;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskService {

    private final SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;
    private final ThreadPoolTaskExecutor computeAsyncTaskExecutor;
    private final FibonacciRepository repository;

    public TaskService(SimpleAsyncTaskExecutor simpleAsyncTaskExecutor,
                       ThreadPoolTaskExecutor computeAsyncTaskExecutor,
                       FibonacciRepository repository) {
        this.simpleAsyncTaskExecutor = simpleAsyncTaskExecutor;
        this.computeAsyncTaskExecutor = computeAsyncTaskExecutor;
        this.repository = repository;
    }

    public CompletableFuture<FibonacciResult> submitComputeTask(FibonacciAlgorithm algorithm, int sequence) {
        var task = new FibonacciComputeTask(algorithm, sequence);
        return computeAsyncTaskExecutor.submitCompletable(task);
    }

    public CompletableFuture<List<Integer>> submitSequenceFilter(int offset, int limit) {
        var task = new FibonacciSequenceFilterTask(repository, offset, limit);
        return simpleAsyncTaskExecutor.submitCompletable(task);
    }
}
