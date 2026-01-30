package com.github.mangila.fibonacci.jobrunr.job.consumer;

import com.github.mangila.fibonacci.jobrunr.job.model.FibonacciComputeRequest;
import com.github.mangila.fibonacci.redis.RedisKey;
import com.github.mangila.fibonacci.redis.RedisRepository;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.jobrunr.scheduling.JobBuilder.aJob;

@ConditionalOnProperty(prefix = "app.consume", name = "enabled", havingValue = "true")
@Component
public class FibonacciConsumeJobHandler implements JobRequestHandler<FibonacciConsumeJobRequest> {

    private static final Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(FibonacciConsumeJobHandler.class));

    private static final List<String> DEFAULT_LABELS = List.of("fibonacci", "compute");

    private final JsonMapper jsonMapper;
    private final JobRequestScheduler jobRequestScheduler;
    private final RedisKey queue;
    private final RedisKey bloomFilter;
    private final RedisRepository redisRepository;

    public FibonacciConsumeJobHandler(JsonMapper jsonMapper,
                                      JobRequestScheduler jobRequestScheduler,
                                      RedisKey queue,
                                      RedisKey bloomFilter,
                                      RedisRepository redisRepository) {
        this.jsonMapper = jsonMapper;
        this.jobRequestScheduler = jobRequestScheduler;
        this.queue = queue;
        this.bloomFilter = bloomFilter;
        this.redisRepository = redisRepository;
    }

    @Override
    public void run(FibonacciConsumeJobRequest jobRequest) throws Exception {
        final int limit = jobRequest.limit();
        for (int i = 0; i < limit; i++) {
            try {
                String json = redisRepository.popQueue(queue);
                if (json != null) {
                    final var payload = jsonMapper.readValue(json, FibonacciComputeRequest.class);
                    final var sequence = payload.sequence();
                    if (redisRepository.checkBloomFilter(bloomFilter, sequence)) {
                        log.info("Skipping: {} - {}", sequence, json);
                        continue;
                    }
                    final var algorithm = payload.algorithm();
                    var uuid = jobRequestScheduler.create(aJob()
                            .scheduleIn(Duration.ofSeconds(ThreadLocalRandom.current().nextInt(1, 10)))
                            .withName("Fibonacci Calculation for sequence: (%s)".formatted(sequence))
                            .withAmountOfRetries(3)
                            .withLabels(DEFAULT_LABELS)
                            .withJobRequest(new FibonacciComputeJobRequest(sequence, algorithm)));
                    redisRepository.addBloomFilter(bloomFilter, sequence);
                    log.info("Scheduled: {} - {}", uuid, json);
                }
            } catch (Exception e) {
                log.error("Error while processing queue: {}", e.getMessage(), e);
            }
        }
    }

}

