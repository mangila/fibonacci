package com.github.mangila.fibonacci.scheduler.jobrunr;

import com.github.mangila.fibonacci.postgres.FibonacciProjection;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.redis.FunctionName;
import com.github.mangila.fibonacci.redis.RedisKey;
import com.github.mangila.fibonacci.redis.RedisRepository;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Component
public class InsertRedisZsetJobHandler implements JobRequestHandler<InsertRedisZsetJobRequest> {

    private static final Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(InsertRedisZsetJobHandler.class));

    private final RedisKey bloomFilterKey;
    private final RedisKey zsetKey;
    private final FunctionName addZset;
    private final JsonMapper jsonMapper;
    private final PostgresRepository postgresRepository;
    private final RedisRepository redisRepository;

    public InsertRedisZsetJobHandler(@Qualifier("bloomFilterKey") RedisKey bloomFilterKey,
                                     @Qualifier("zsetKey") RedisKey zsetKey,
                                     @Qualifier("addZset") FunctionName addZset,
                                     JsonMapper jsonMapper,
                                     PostgresRepository postgresRepository,
                                     RedisRepository redisRepository) {
        this.bloomFilterKey = bloomFilterKey;
        this.zsetKey = zsetKey;
        this.addZset = addZset;
        this.jsonMapper = jsonMapper;
        this.postgresRepository = postgresRepository;
        this.redisRepository = redisRepository;
    }

    /**
     * Locks the metadata tables rows from the last inserted sequence to the limit,
     * then query Postgres for a projection, serialize it to JSON, and invokes the add_zset redis function.
     */
    @Transactional
    @Override
    public void run(InsertRedisZsetJobRequest jobRequest) {
        try {
            postgresRepository.streamMetadataIdWhereNotSentToZsetLocked(jobRequest.limit(), stream -> {
                stream.forEach(sequence -> {
                    log.info("Adding to redis sorted set (zset): {}", sequence);
                    try {
                        FibonacciProjection projection = postgresRepository.queryBySequence(sequence)
                                .orElseThrow();
                        final String score = String.valueOf(sequence);
                        final String member = jsonMapper.writeValueAsString(projection);
                        redisRepository.functionCall(
                                addZset,
                                List.of(zsetKey, bloomFilterKey),
                                List.of(score, member)
                        );
                        postgresRepository.upsertMetadata(sequence,
                                true,
                                false);
                    } catch (Exception e) {
                        log.error("Error while adding to redis sorted set (zset): {}", sequence, e);
                    }
                    log.info("Added to redis sorted set (zset): {}", sequence);
                });
            });
        } catch (Exception e) {
            log.error("Error while processing stream log request", e);
            throw e; // rollback
        }
    }
}
