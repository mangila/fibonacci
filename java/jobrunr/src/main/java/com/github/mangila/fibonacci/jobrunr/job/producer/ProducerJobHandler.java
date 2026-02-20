package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.postgres.FibonacciMetadataProjection;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;

@Service
public class ProducerJobHandler implements JobRequestHandler<ProducerJobRequest> {

    private static final Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(ProducerJobHandler.class));

    private final PostgresRepository repository;

    public ProducerJobHandler(PostgresRepository repository) {
        this.repository = repository;
    }

    /**
     * if an exception occurs, the job will be retried
     * trade some resources for NO-OP batches for simplicity
     */
    @Override
    public void run(ProducerJobRequest jobRequest) throws Exception {
        final var limit = jobRequest.limit();
        final var algorithm = jobRequest.algorithm();
        final var batchSize = jobRequest.batchSize();
        log.info("Generating {} fibonacci numbers with algorithm {}", limit, algorithm);
        var batchBuffer = new ArrayList<FibonacciMetadataProjection>(batchSize);
        for (int i = 1; i <= limit; i++) {
            var metadata = FibonacciMetadataProjection.newInsert(i, algorithm.name());
            if (log.isDebugEnabled()) {
                log.debug("Produce fibonacci number: {}", metadata);
            }
            batchBuffer.add(metadata);
            // micro batching
            if (batchBuffer.size() >= batchSize) {
                if (log.isDebugEnabled()) {
                    log.debug("Flushing micro batch - {}", batchBuffer);
                }
                repository.batchInsertMetadata(batchBuffer);
                batchBuffer.clear();
            }
        }
        // flush the last batch
        if (!CollectionUtils.isEmpty(batchBuffer)) {
            repository.batchInsertMetadata(batchBuffer);
        }
    }
}
