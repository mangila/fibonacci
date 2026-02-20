package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.postgres.FibonacciMetadataProjection;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class ProducerJobHandler implements JobRequestHandler<ProducerJobRequest> {

    private static final Logger log = LoggerFactory.getLogger(ProducerJobHandler.class);

    private static final int BATCH_SIZE = 100;

    private final PostgresRepository repository;

    public ProducerJobHandler(PostgresRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ProducerJobRequest jobRequest) throws Exception {
        final var limit = jobRequest.limit();
        final var algorithm = jobRequest.algorithm();
        log.info("Scheduling {} Fibonacci calculations", limit);
        var metadataProjections = new ArrayList<FibonacciMetadataProjection>();
        for (int i = 1; i <= limit; i++) {
            var metadata = new FibonacciMetadataProjection(i, false, algorithm.name());
            metadataProjections.add(metadata);
            if (metadataProjections.size() >= BATCH_SIZE) {
                repository.batchInsertMetadata(metadataProjections);
                metadataProjections.clear();
            }
        }
        repository.batchInsertMetadata(metadataProjections);
    }
}
