package com.github.mangila.fibonacci.jobrunr.job.consumer;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.jobrunr.job.consumer.compute.ComputeJobRequest;
import com.github.mangila.fibonacci.jobrunr.job.consumer.compute.ComputeScheduler;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class ConsumerJobHandler implements JobRequestHandler<ConsumerJobRequest> {

    private static final Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(ConsumerJobHandler.class));

    private final ComputeScheduler computeScheduler;
    private final PostgresRepository postgresRepository;

    public ConsumerJobHandler(ComputeScheduler computeScheduler,
                              PostgresRepository postgresRepository) {
        this.computeScheduler = computeScheduler;
        this.postgresRepository = postgresRepository;
    }

    @Transactional
    @Override
    public void run(ConsumerJobRequest jobRequest) throws Exception {
        final int limit = jobRequest.limit();
        postgresRepository.streamMetadataWhereComputedFalseLocked(limit, metadataStream -> {
            metadataStream.forEach(projection -> {
                var fibonacciAlgorithm = FibonacciAlgorithm.valueOf(projection.algorithm());
                var uuid = computeScheduler.schedule(new ComputeJobRequest(projection.id(), fibonacciAlgorithm));
                log.info("Scheduled: {} - {}", uuid, projection);
            });
        });
    }
}

