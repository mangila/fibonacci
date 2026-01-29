package com.github.mangila.fibonacci.jobrunr.job;

import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.redis.FunctionName;
import com.github.mangila.fibonacci.redis.RedisKey;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.Collections;
import java.util.List;

@Component
public class DrainZsetJobHandler implements JobRequestHandler<DrainZsetJobRequest> {

    private static final Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(DrainZsetJobHandler.class));

    private final JedisConnectionFactory jedisConnectionFactory;
    private final PostgresRepository postgresRepository;
    private final RedisKey zsetKey;
    private final RedisKey streamKey;
    private final RedisKey valueKey;
    private final FunctionName drainZset;

    public DrainZsetJobHandler(JedisConnectionFactory jedisConnectionFactory,
                               PostgresRepository postgresRepository,
                               RedisKey zsetKey,
                               RedisKey streamKey,
                               RedisKey valueKey,
                               FunctionName drainZset) {
        this.jedisConnectionFactory = jedisConnectionFactory;
        this.postgresRepository = postgresRepository;
        this.zsetKey = zsetKey;
        this.streamKey = streamKey;
        this.valueKey = valueKey;
        this.drainZset = drainZset;
    }

    @Override
    public void run(DrainZsetJobRequest jobRequest) throws Exception {
        final int limit = jobRequest.limit();
        List<Object> results;
        try (Jedis jedis = (Jedis) jedisConnectionFactory.getConnection().getNativeConnection()) {
            var pipeline = jedis.pipelined();
            for (int i = 0; i < limit; i++) {
                pipeline.fcall(drainZset.value(),
                        List.of(zsetKey.value(), streamKey.value(), valueKey.value()),
                        Collections.emptyList());
            }
            results = pipeline.syncAndReturnAll();
        }
        // TODO: update DB
        for (Object result : results) {
            if (result instanceof String) {
                log.info("Drained zset: {}", result);
            }
            if (result instanceof JedisDataException) {
                log.warn("Error while draining zset: {}", result);
            }
        }
    }
}
