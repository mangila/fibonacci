package com.github.mangila.fibonacci.jobrunr.job.consumer;

import com.github.mangila.fibonacci.jobrunr.job.consumer.task.FibonacciComputeTask;
import com.github.mangila.fibonacci.postgres.FibonacciMetadataProjection;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.TimeUnit;

@Component
public class FibonacciComputeHandler implements JobRequestHandler<FibonacciComputeJobRequest> {

    private static final Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(FibonacciComputeHandler.class));

    private final ThreadPoolTaskExecutor computeAsyncTaskExecutor;
    private final PostgresRepository postgresRepository;
    private final TransactionTemplate transactionTemplate;

    public FibonacciComputeHandler(ThreadPoolTaskExecutor computeAsyncTaskExecutor,
                                   PostgresRepository postgresRepository,
                                   TransactionTemplate transactionTemplate) {
        this.computeAsyncTaskExecutor = computeAsyncTaskExecutor;
        this.postgresRepository = postgresRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void run(FibonacciComputeJobRequest jobRequest) {
        final var context = jobContext();
        final var request = jobRequest;
        final var progressBar = context.progressBar(100);
        var result = context.runStepOnce("compute", () -> {
            final var algorithm = request.algorithm();
            final var sequence = request.sequence();
            var task = new FibonacciComputeTask(algorithm, sequence);
            var future = computeAsyncTaskExecutor.submitCompletable(task)
                    .orTimeout(3, TimeUnit.MINUTES)
                    .handle((fibonacciResult, throwable) -> {
                        if (throwable != null) {
                            throw new RuntimeException("Failed to compute Fibonacci sequence", throwable);
                        }
                        return fibonacciResult;
                    });
            return future.join();
        });
        progressBar.setProgress(50);
        boolean write = context.runStepOnce("insert", () -> {
            // Start a new transaction after the compute task is finished
            // no need to wrap it inside a @Transactional and take a connection during the compute time
            return transactionTemplate.execute(_ -> {
                var insert = postgresRepository.insert(result.sequence(), result.result(), result.precision());
                if (insert.isPresent()) {
                    var metadata = new FibonacciMetadataProjection(
                            result.sequence(),
                            false,
                            false);
                    postgresRepository.upsertMetadata(metadata);
                    return true;
                }
                return false;
            });
        });
        if (write) {
            log.info("Fibonacci sequence: {} was computed and stored", result.sequence());
        } else {
            log.info("Fibonacci sequence: {} is already computed and stored", result.sequence());
        }
        progressBar.setProgress(100);
    }

    @Override
    public JobContext jobContext() {
        return JobRequestHandler.super.jobContext();
    }
}
