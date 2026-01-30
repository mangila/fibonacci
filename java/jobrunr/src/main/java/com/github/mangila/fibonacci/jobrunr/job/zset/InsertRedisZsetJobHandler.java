package com.github.mangila.fibonacci.jobrunr.job.zset;

import com.github.mangila.fibonacci.postgres.FibonacciMetadataProjection;
import com.github.mangila.fibonacci.postgres.FibonacciProjection;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.redis.RedisKey;
import com.github.mangila.fibonacci.redis.RedisRepository;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;

@Component
public class InsertRedisZsetJobHandler implements JobRequestHandler<InsertRedisZsetJobRequest> {

    private static final Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(InsertRedisZsetJobHandler.class));

    private final RedisKey zset;
    private final JsonMapper jsonMapper;
    private final PostgresRepository postgresRepository;
    private final RedisRepository redisRepository;

    public InsertRedisZsetJobHandler(RedisKey zset,
                                     JsonMapper jsonMapper,
                                     PostgresRepository postgresRepository,
                                     RedisRepository redisRepository) {
        this.zset = zset;
        this.jsonMapper = jsonMapper;
        this.postgresRepository = postgresRepository;
        this.redisRepository = redisRepository;
    }

    @Override
    public void run(InsertRedisZsetJobRequest jobRequest) {
        try {
            postgresRepository.streamMetadataLocked(jobRequest.limit(), stream -> {
                var success = new ArrayList<FibonacciMetadataProjection>();
                stream.forEach(sequence -> {
                    try {
                        FibonacciProjection projection = postgresRepository.queryBySequence(sequence)
                                .orElseThrow();
                        final String member = jsonMapper.writeValueAsString(projection);
                        log.info("Adding to redis sorted set (zset): {} -> {}", sequence, member);
                        redisRepository.addZset(
                                zset,
                                sequence,
                                member
                        );
                        success.add(new FibonacciMetadataProjection(sequence,
                                true,
                                false));
                    } catch (Exception e) {
                        log.error("Error while adding to redis sorted set (zset): {}", sequence, e);
                    }
                });
                if (!CollectionUtils.isEmpty(success)) {
                    postgresRepository.batchUpsertMetadata(success);
                }
            });
        } catch (Exception e) {
            log.error("Error while processing stream log request", e);
            throw e; // rollback
        }
    }
}
