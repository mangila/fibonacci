package com.github.mangila.fibonacci.jobrunr.job.zset.drain;

import com.github.mangila.fibonacci.postgres.FibonacciMetadataProjection;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.redis.FunctionName;
import com.github.mangila.fibonacci.redis.RedisKey;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class DrainZsetJobHandler implements JobRequestHandler<DrainZsetJobRequest> {

    private static final Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(DrainZsetJobHandler.class));

    private final JedisConnectionFactory jedisConnectionFactory;
    private final PostgresRepository postgresRepository;
    private final TransactionTemplate transactionTemplate;
    private final FunctionName drainZset;
    private final List<String> keys;

    public DrainZsetJobHandler(JedisConnectionFactory jedisConnectionFactory,
                               PostgresRepository postgresRepository,
                               TransactionTemplate transactionTemplate,
                               RedisKey zset,
                               RedisKey stream,
                               RedisKey value,
                               FunctionName drainZset) {
        this.jedisConnectionFactory = jedisConnectionFactory;
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
        List<Object> pipelineResults;
        try (Jedis jedis = (Jedis) jedisConnectionFactory.getConnection().getNativeConnection()) {
            var pipeline = jedis.pipelined();
            for (int i = 0; i < limit; i++) {
                jedis.fcall(drainZset.value(), keys, Collections.emptyList());
            }
            pipelineResults = pipeline.syncAndReturnAll();
        }
        var success = new ArrayList<FibonacciMetadataProjection>();
        for (Object result : pipelineResults) {
            var metadata = handleResult(result);
            if (metadata != null) {
                success.add(metadata);
            }
        }
        if (!CollectionUtils.isEmpty(success)) {
            transactionTemplate.executeWithoutResult(_ -> {
                postgresRepository.batchUpsertMetadata(success);
            });
            log.info("Successfully drained {} records", success.size());
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
