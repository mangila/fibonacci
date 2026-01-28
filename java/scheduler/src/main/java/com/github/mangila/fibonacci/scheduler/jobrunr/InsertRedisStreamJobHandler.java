package com.github.mangila.fibonacci.scheduler.jobrunr;

import com.github.mangila.fibonacci.postgres.FibonacciProjection;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.redis.RedisRepository;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Map;

@Component
public class InsertRedisStreamJobHandler implements JobRequestHandler<InsertRedisStreamJobRequest> {

    private static final Logger log = LoggerFactory.getLogger(InsertRedisStreamJobHandler.class);
    private final PostgresRepository postgresRepository;
    private final RedisRepository redisRepository;

    public InsertRedisStreamJobHandler(PostgresRepository postgresRepository,
                                       RedisRepository redisRepository) {
        this.postgresRepository = postgresRepository;
        this.redisRepository = redisRepository;
    }

    @Transactional
    @Override
    public void run(InsertRedisStreamJobRequest jobRequest) throws Exception {
        log.info("Processing stream log request: {}", jobRequest);
        var redisStreamIds = new ArrayList<String>();
        try {
            postgresRepository.streamMetadataLocked(jobRequest.limit(), stream -> {
                stream.forEach(sequence -> {
                    log.info("Processing stream log entry: {}", sequence);
                    log.info("Adding to redis stream: {}", sequence);
                    FibonacciProjection projection = postgresRepository.queryBySequence(sequence)
                            .orElseThrow();
                    Map<String, String> streamData = projection.asStringMap();
                    String streamId = redisRepository.addToStream(sequence, streamData);
                    redisStreamIds.add(streamId);
                    postgresRepository.upsertMetadata(sequence, true);
                    log.info("sent to stream was ok: {}", sequence);
                });
            });
        } catch (Exception e) {
            log.error("Error while processing stream log request", e);
            if (!redisStreamIds.isEmpty()) {
                redisRepository.removeFromStream(redisStreamIds);
            }
            throw e; // rollback
        }
    }
}
