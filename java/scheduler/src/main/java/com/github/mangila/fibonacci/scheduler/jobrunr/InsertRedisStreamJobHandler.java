package com.github.mangila.fibonacci.scheduler.jobrunr;

import com.github.mangila.fibonacci.postgres.FibonacciProjection;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.redis.RedisRepository;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.StreamEntryID;

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

    /**
     * Locks the metadata tables rows from the last inserted sequence to the limit,
     * then query Postgres for a projection, adds it to the redis stream, and updates the metadata table
     * <p>
     * Will try to delete the inserted stream entry ids from the redis stream on an exception.
     * This is a little fuzzy there are some edge cases to consider using this rollback approach
     * network issues, redis server restarts, etc.
     */
    @Transactional
    @Override
    public void run(InsertRedisStreamJobRequest jobRequest) throws Exception {
        log.info("Processing stream log request: {}", jobRequest);
        var redisStreamIds = new ArrayList<StreamEntryID>();
        try {
            postgresRepository.streamMetadataLocked(jobRequest.limit(), stream -> {
                stream.forEach(sequence -> {
                    log.info("Adding to redis stream: {}", sequence);
                    FibonacciProjection projection = postgresRepository.queryBySequence(sequence)
                            .orElseThrow();
                    Map<String, String> streamData = projection.asStringMap();
                    StreamEntryID streamId = redisRepository.addToStream(sequence, streamData);
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
