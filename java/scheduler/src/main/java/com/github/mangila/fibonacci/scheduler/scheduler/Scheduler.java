package com.github.mangila.fibonacci.scheduler.scheduler;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.core.model.FibonacciComputeCommand;
import com.github.mangila.fibonacci.core.model.FibonacciResult;
import com.github.mangila.fibonacci.scheduler.properties.ComputeProperties;
import com.github.mangila.fibonacci.scheduler.repository.FibonacciRepository;
import com.github.mangila.fibonacci.scheduler.task.FibonacciComputeTask;
import io.github.mangila.ensure4j.Ensure;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class Scheduler {

    private static final Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(Scheduler.class));

    private final ThreadPoolTaskExecutor computeAsyncTaskExecutor;
    private final JobScheduler jobScheduler;
    private final FibonacciRepository repository;
    private final SequenceCache sequenceCache;
    private final ComputeProperties computeProperties;

    public Scheduler(@Qualifier("computeAsyncTaskExecutor") ThreadPoolTaskExecutor computeAsyncTaskExecutor,
                     JobScheduler jobScheduler,
                     FibonacciRepository repository,
                     SequenceCache sequenceCache,
                     ComputeProperties computeProperties) {
        this.computeAsyncTaskExecutor = computeAsyncTaskExecutor;
        this.jobScheduler = jobScheduler;
        this.repository = repository;
        this.sequenceCache = sequenceCache;
        this.computeProperties = computeProperties;
    }

    public void scheduleFibonacciCalculations(FibonacciComputeCommand command) {
        final int start = command.start();
        final int end = command.end();
        Ensure.isTrue(computeProperties.getMax() >= end, "End sequence must be within the configured max limit");
        final FibonacciAlgorithm algorithm = command.algorithm();
        Stream<Integer> sequenceStream = IntStream.range(start, end).boxed();
        jobScheduler.enqueue(sequenceStream, (sequence) -> computeFibonacci(algorithm, sequence));
    }

    @Job(name = "Fibonacci job for number %1", retries = 3, labels = "compute")
    public void computeFibonacci(FibonacciAlgorithm algorithm, int sequence) {
        if (!sequenceCache.tryCompute(sequence)) {
            log.info("Sequence {} is already computed or in flight", sequence);
            return;
        }
        CompletableFuture<FibonacciResult> future = null;
        try {
            future = computeAsyncTaskExecutor.submitCompletable(new FibonacciComputeTask(algorithm, sequence))
                    .orTimeout(3, TimeUnit.MINUTES);
            FibonacciResult result = future.join();
            repository.insert(result);
            sequenceCache.put(sequence);
        } catch (Exception e) {
            log.error("Error while computing sequence {}", sequence, e);
            if (future != null) {
                future.cancel(true);
            }
            throw e;
        } finally {
            sequenceCache.release(sequence);
        }
    }
}
