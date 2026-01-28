package com.github.mangila.fibonacci.scheduler.jobrunr;

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

    Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(FibonacciComputeHandler.class));

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

    /**
     * Spawns a platform thread to run the heavy fibonacci computation.
     * Then proceed to insert the result to Postgres.
     * If the value is already present in Postgres it has already been computed.
     * <p>
     * Outcomes for a double write - how that could happen:
     * <br>
     * the Bloom filter missed it with its false positive rate,
     * OR
     * we have a race condition somewhere.
     */
    @Override
    public void run(FibonacciComputeJobRequest jobRequest) {
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
        // Start a new transaction after the compute task is finished
        // no need to wrap it inside a @Transactional and take a connection during the compute time
        var write = transactionTemplate.execute(tx -> {
            var insert = postgresRepository.insert(result.sequence(), result.result(), result.precision());
            if (insert.isPresent()) {
                postgresRepository.upsertMetadata(result.sequence(), false);
                return true;
            }
            return false;
        });
        if (write) {
            log.info("Fibonacci sequence {} computed and stored", result.sequence());
        } else {
            log.info("Fibonacci sequence {} already computed", result.sequence());
        }
    }

    @Override
    public JobContext jobContext() {
        return JobRequestHandler.super.jobContext();
    }
}
