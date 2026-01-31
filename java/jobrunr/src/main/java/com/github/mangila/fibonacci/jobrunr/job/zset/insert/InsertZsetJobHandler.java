package com.github.mangila.fibonacci.jobrunr.job.zset.insert;

import com.github.mangila.fibonacci.jobrunr.job.zset.model.StepSuccess;
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
        final var context = jobContext();
        final var limit = jobRequest.limit();
        final var stepSuccess = context.runStepOnce("insert", () -> {
            var success = new ArrayList<FibonacciMetadataProjection>();
            postgresRepository.streamMetadataWhereSentToZsetIsFalseLocked(limit, stream -> {
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
            });
            return new StepSuccess(success);
        });
        final var metadata = stepSuccess.metadata();
        if (!CollectionUtils.isEmpty(metadata)) {
            postgresRepository.batchUpsertMetadata(metadata);
            log.info("Successfully added {} sequences to redis sorted set (zset)", metadata.size());
        } else {
            log.info("No sequences added to redis sorted set (zset)");
        }
    }
}
