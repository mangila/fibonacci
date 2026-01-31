package com.github.mangila.fibonacci.jobrunr.job.zset.insert;

import com.github.mangila.fibonacci.postgres.FibonacciMetadataProjection;
import com.github.mangila.fibonacci.postgres.FibonacciProjection;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.redis.RedisKey;
import com.github.mangila.fibonacci.redis.RedisRepository;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;

public class InsertZsetJobHandler implements JobRequestHandler<InsertZsetJobRequest> {

    private static final Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(InsertZsetJobHandler.class));

    private final RedisKey zset;
    private final JsonMapper jsonMapper;
    private final PostgresRepository postgresRepository;
    private final RedisRepository redisRepository;

    public InsertZsetJobHandler(RedisKey zset,
                                JsonMapper jsonMapper,
                                PostgresRepository postgresRepository,
                                RedisRepository redisRepository) {
        this.zset = zset;
        this.jsonMapper = jsonMapper;
        this.postgresRepository = postgresRepository;
        this.redisRepository = redisRepository;
    }

    @Override
    public void run(InsertZsetJobRequest jobRequest) {
        try {
            postgresRepository.streamMetadataWhereSentToZsetIsFalseLocked(jobRequest.limit(), stream -> {
                var success = new ArrayList<FibonacciMetadataProjection>();
                stream.forEach(sequence -> {
                    try {
                        final FibonacciProjection projection = postgresRepository.queryBySequence(sequence)
                                .orElseThrow();
                        final String member = jsonMapper.writeValueAsString(projection);
                        redisRepository.addZset(zset, sequence, member);
                        success.add(new FibonacciMetadataProjection(sequence,
                                true,
                                false));
                    } catch (Exception e) {
                        log.error("Error while adding to redis sorted set (zset): {}", sequence, e);
                    }
                });
                if (!CollectionUtils.isEmpty(success)) {
                    postgresRepository.batchUpsertMetadata(success);
                    log.info("Successfully added {} sequences to redis sorted set (zset)", success.size());
                } else {
                    log.info("No sequences added to redis sorted set (zset)");
                }
            });
        } catch (Exception e) {
            log.error("Error while processing stream log request", e);
            throw e; // rollback
        }
    }
}
