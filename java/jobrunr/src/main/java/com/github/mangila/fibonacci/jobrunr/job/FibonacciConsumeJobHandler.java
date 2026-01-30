package com.github.mangila.fibonacci.jobrunr.job;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.core.FibonacciComputeRequest;
import com.github.mangila.fibonacci.redis.RedisKey;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
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
    private final RedisKey queueKey;

    public FibonacciConsumeJobHandler(JsonMapper jsonMapper,
                                      JobRequestScheduler jobRequestScheduler,
                                      JedisConnectionFactory jedisConnectionFactory,
                                      RedisKey queueKey) {
        this.jsonMapper = jsonMapper;
        this.jobRequestScheduler = jobRequestScheduler;
        this.jedisConnectionFactory = jedisConnectionFactory;
        this.queueKey = queueKey;
    }

    @Override
    public void run(FibonacciConsumeJobRequest jobRequest) throws Exception {
        final int limit = jobRequest.limit();
        List<Object> results;
        try (Jedis jedis = (Jedis) jedisConnectionFactory.getConnection().getNativeConnection()) {
            var pipeline = jedis.pipelined();
            for (int i = 0; i < limit; i++) {
                pipeline.lpop(queueKey.value());
            }
            results = pipeline.syncAndReturnAll();
        }
        for (Object result : results) {
            if (result instanceof String) {
                System.out.println("Consumed: " + result);
            }
        }

        //log.info("Received message: {}", pop);
        //String data = pop.get(1);
//        FibonacciComputeRequest request = jsonMapper.readValue("", FibonacciComputeRequest.class);
//        final int sequence = request.sequence();
//        final FibonacciAlgorithm algorithm = request.algorithm();
//        log.info("Enqueue job for sequence: {}", sequence);
//        jobRequestScheduler.create(aJob()
//                .scheduleIn(Duration.ofSeconds(ThreadLocalRandom.current().nextInt(1, 10)))
//                .withId(UUID.randomUUID())
//                .withName("Fibonacci Calculation for sequence: (%s)")
//                .withAmountOfRetries(3)
//                .withLabels(DEFAULT_LABELS)
//                .withJobRequest(new FibonacciComputeJobRequest(sequence, algorithm)));
    }

    public static boolean canEnqueue(List<String> list) {
        return list != null && list.size() == 2;
    }
}

