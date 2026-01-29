package com.github.mangila.fibonacci.scheduler.jobrunr;

import com.github.mangila.fibonacci.postgres.FibonacciProjection;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.redis.RedisConfig;
import com.github.mangila.fibonacci.redis.RedisRepository;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Component
public class InsertRedisZsetJobHandler implements JobRequestHandler<InsertRedisZsetJobRequest> {

    private static final Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(InsertRedisZsetJobHandler.class));

    private final JsonMapper jsonMapper;
    private final PostgresRepository postgresRepository;
    private final RedisRepository redisRepository;

    public InsertRedisZsetJobHandler(JsonMapper jsonMapper,
                                     PostgresRepository postgresRepository,
                                     RedisRepository redisRepository) {
        this.jsonMapper = jsonMapper;
        this.postgresRepository = postgresRepository;
        this.redisRepository = redisRepository;
    }

    /**
     * Locks the metadata tables rows from the last inserted sequence to the limit,
     * then query Postgres for a projection, serialize it to JSON, and adds it to the sorted set in Redis(zset).
     */
    @Transactional
    @Override
    public void run(InsertRedisZsetJobRequest jobRequest) {
        try {
            postgresRepository.streamMetadataLocked(jobRequest.limit(), stream -> {
                stream.forEach(sequence -> {
                    log.info("Adding to redis sorted set (zset): {}", sequence);
                    FibonacciProjection projection = postgresRepository.queryBySequence(sequence)
                            .orElseThrow();
                    var json = jsonMapper.writeValueAsString(projection);
                    redisRepository.sortedSetAdd(
                            RedisConfig.ZSET_KEY,
                            sequence,
                            json
                    );
                });
            });
        } catch (Exception e) {
            log.error("Error while processing stream log request", e);
            throw e; // rollback
        }
    }
}
