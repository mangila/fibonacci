package com.github.mangila.fibonacci.scheduler.jobrunr;

import com.github.mangila.fibonacci.postgres.FibonacciRepository;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FibonacciComputeHandler implements JobRequestHandler<FibonacciComputeRequest> {

    private final ThreadPoolTaskExecutor computeAsyncTaskExecutor;
    private final FibonacciRepository repository;

    public FibonacciComputeHandler(ThreadPoolTaskExecutor computeAsyncTaskExecutor,
                                   FibonacciRepository repository) {
        this.computeAsyncTaskExecutor = computeAsyncTaskExecutor;
        this.repository = repository;
    }

    @Override
    public void run(FibonacciComputeRequest jobRequest) {
        final var algorithm = jobRequest.algorithm();
        final var sequence = jobRequest.sequence();
        var task = new FibonacciComputeTask(algorithm, sequence);
        var future = computeAsyncTaskExecutor.submitCompletable(task)
                .orTimeout(3, TimeUnit.MINUTES)
                .handle((fibonacciResult, throwable) -> {
                    if (throwable != null) {
                        throw new RuntimeException("Failed to compute Fibonacci sequence", throwable);
                    }
                    return fibonacciResult;
                });
        FibonacciComputeResult result = future.join();
        repository.insert(result.sequence(), result.result(), result.precision());
        // TODO: update bloom filter
        // TODO: add redis stream log
    }

    @Override
    public JobContext jobContext() {
        return JobRequestHandler.super.jobContext();
    }
}
