package com.github.mangila.fibonacci.jobrunr.job.consumer;

import com.github.mangila.fibonacci.jobrunr.job.consumer.compute.ComputeJobRequest;
import com.github.mangila.fibonacci.jobrunr.job.consumer.compute.ComputeScheduler;
import com.github.mangila.fibonacci.postgres.FibonacciMetadataProjection;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.shared.FibonacciAlgorithm;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
        postgresRepository.streamMetadataWhereScheduledFalseLocked(limit, metadataStream -> {
            metadataStream.forEach(projection -> {
                if (log.isDebugEnabled()) {
                    log.debug("Processing: {}", projection);
                }
                var fibonacciAlgorithm = FibonacciAlgorithm.valueOf(projection.algorithm());
                var uuid = computeScheduler.schedule(new ComputeJobRequest(projection.id(), fibonacciAlgorithm));
                postgresRepository.upsertMetadata(FibonacciMetadataProjection.scheduled(projection.id(), projection.algorithm()));
                log.info("Scheduled for computation: {} - {}", uuid, projection);
            });
        });
    }
}

