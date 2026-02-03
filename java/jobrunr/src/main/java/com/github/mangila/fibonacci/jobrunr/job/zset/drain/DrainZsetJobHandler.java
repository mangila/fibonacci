package com.github.mangila.fibonacci.jobrunr.job.zset.drain;

import com.github.mangila.fibonacci.jobrunr.job.zset.model.StepSuccess;
import com.github.mangila.fibonacci.postgres.FibonacciMetadataProjection;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.redis.FunctionName;
import com.github.mangila.fibonacci.redis.RedisKey;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class DrainZsetJobHandler implements JobRequestHandler<DrainZsetJobRequest> {

    private static final Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(DrainZsetJobHandler.class));

    private final JedisPooled jedisPooled;
    private final PostgresRepository postgresRepository;
    private final TransactionTemplate transactionTemplate;
    private final FunctionName drainZset;
    private final List<String> keys;

    public DrainZsetJobHandler(JedisPooled jedisPooled,
                               PostgresRepository postgresRepository,
                               TransactionTemplate transactionTemplate,
                               RedisKey zset,
                               RedisKey stream,
                               RedisKey value,
                               FunctionName drainZset) {
        this.jedisPooled = jedisPooled;
        this.postgresRepository = postgresRepository;
        this.transactionTemplate = transactionTemplate;
        this.keys = Stream.of(zset, stream, value)
                .map(RedisKey::value)
                .toList();
        this.drainZset = drainZset;
    }


    @Override
    public void run(DrainZsetJobRequest jobRequest) throws Exception {
        final int limit = jobRequest.limit();
        final var context = jobContext();
        var stepSuccess = context.runStepOnce("drain", () -> {
            try (Pipeline pipeline = jedisPooled.pipelined()) {
                for (int i = 0; i < limit; i++) {
                    pipeline.fcall(drainZset.value(), keys, Collections.emptyList());
                }
                var successResults = pipeline.syncAndReturnAll()
                        .stream()
                        .map(DrainZsetJobHandler::handleResult)
                        .filter(Objects::nonNull)
                        .toList();
                return new StepSuccess(successResults);
            }
        });
        final var metadata = stepSuccess.metadata();
        if (!CollectionUtils.isEmpty(metadata)) {
            transactionTemplate.executeWithoutResult(_ -> {
                postgresRepository.batchUpsertMetadata(metadata);
            });
            log.info("Successfully drained {} records", metadata.size());
        } else {
            log.info("Nothing to drain from zset");
        }
    }


    @Nullable
    private static FibonacciMetadataProjection handleResult(Object result) {
        return switch (result) {
            case String s -> {
                if (s.equals("ZSET_EMPTY")) {
                    yield null;
                } else if (s.startsWith("SEQUENCE_MISMATCH")) {
                    log.warn("{}", s);
                    yield null;
                } else {
                    int sequence = Integer.parseInt(s);
                    yield new FibonacciMetadataProjection(sequence, true, true);
                }
            }
            case JedisDataException e -> {
                log.error("{}", e.getMessage(), e);
                yield null;
            }
            default -> throw new IllegalStateException("Unexpected value: " + result);
        };
    }
}
