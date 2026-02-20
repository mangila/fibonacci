package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.postgres.FibonacciMetadataProjection;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;

public class ProducerJobHandler implements JobRequestHandler<ProducerJobRequest> {

    private static final Logger log = LoggerFactory.getLogger(ProducerJobHandler.class);

    private static final int BATCH_SIZE = 100;

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
        log.info("Generating {} fibonacci numbers with algorithm {}", limit, algorithm);
        var batchBuffer = new ArrayList<FibonacciMetadataProjection>(BATCH_SIZE);
        for (int i = 1; i <= limit; i++) {
            var metadata = new FibonacciMetadataProjection(i, false, algorithm.name());
            if (log.isDebugEnabled()) {
                log.debug("Produce fibonacci number: {}", metadata);
            }
            batchBuffer.add(metadata);
            // micro batching
            if (batchBuffer.size() >= BATCH_SIZE) {
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
