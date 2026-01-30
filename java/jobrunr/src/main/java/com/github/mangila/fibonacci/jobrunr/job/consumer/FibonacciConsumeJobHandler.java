package com.github.mangila.fibonacci.jobrunr.job.consumer;

import com.github.mangila.fibonacci.jobrunr.job.model.FibonacciComputeRequest;
import com.github.mangila.fibonacci.redis.RedisKey;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.jobrunr.scheduling.JobBuilder.aJob;

@Component
public class FibonacciConsumeJobHandler implements JobRequestHandler<FibonacciConsumeJobRequest> {

    private static final Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(FibonacciConsumeJobHandler.class));

    private static final List<String> DEFAULT_LABELS = List.of("fibonacci", "compute");

    private final JsonMapper jsonMapper;
    private final JobRequestScheduler jobRequestScheduler;
    private final JedisConnectionFactory jedisConnectionFactory;
    private final RedisKey queue;

    public FibonacciConsumeJobHandler(JsonMapper jsonMapper,
                                      JobRequestScheduler jobRequestScheduler,
                                      JedisConnectionFactory jedisConnectionFactory,
                                      RedisKey queue) {
        this.jsonMapper = jsonMapper;
        this.jobRequestScheduler = jobRequestScheduler;
        this.jedisConnectionFactory = jedisConnectionFactory;
        this.queue = queue;
    }

    @Override
    public void run(FibonacciConsumeJobRequest jobRequest) throws Exception {
        final int limit = jobRequest.limit();
        final var context = jobContext();
        var progressBar = context.progressBar(limit);
        List<Object> pipelineResults;
        try (Jedis jedis = (Jedis) jedisConnectionFactory.getConnection().getNativeConnection()) {
            var pipeline = jedis.pipelined();
            for (int i = 0; i < limit; i++) {
                pipeline.lpop(queue.value());
            }
            pipelineResults = pipeline.syncAndReturnAll();
        }
        for (Object result : pipelineResults) {
            if (result == null) {
                continue;
            }
            handleResult(result);

        }
    }

    public void handleResult(@NonNull Object result) {
        switch (result) {
            case String json -> {
                final var payload = jsonMapper.readValue(json, FibonacciComputeRequest.class);
                final var sequence = payload.sequence();
                final var algorithm = payload.algorithm();
                var uuid = jobRequestScheduler.create(aJob()
                        .scheduleIn(Duration.ofSeconds(ThreadLocalRandom.current().nextInt(1, 10)))
                        .withName("Fibonacci Calculation for sequence: (%s)".formatted(sequence))
                        .withAmountOfRetries(3)
                        .withLabels(DEFAULT_LABELS)
                        .withJobRequest(new FibonacciComputeJobRequest(sequence, algorithm)));
                log.info("JSON: {} - {}", json, uuid);
            }
            case JedisDataException e -> log.error("{}", e.getMessage(), e);
            default -> throw new IllegalStateException("Unexpected value: " + result);
        }
    }

}

