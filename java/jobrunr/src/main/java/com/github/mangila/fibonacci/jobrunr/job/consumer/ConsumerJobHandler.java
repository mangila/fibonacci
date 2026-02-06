package com.github.mangila.fibonacci.jobrunr.job.consumer;

import com.github.mangila.fibonacci.jobrunr.job.consumer.compute.ComputeJobRequest;
import com.github.mangila.fibonacci.jobrunr.job.consumer.compute.ComputeScheduler;
import com.github.mangila.fibonacci.jobrunr.job.model.FibonacciComputeRequest;
import com.github.mangila.fibonacci.redis.RedisKey;
import com.github.mangila.fibonacci.redis.RedisRepository;
import org.intellij.lang.annotations.Language;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

public class ConsumerJobHandler implements JobRequestHandler<ConsumerJobRequest> {

    private static final Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(ConsumerJobHandler.class));

    private final JsonMapper jsonMapper;
    private final ComputeScheduler computeScheduler;
    private final RedisKey queue;
    private final RedisKey bloomFilter;
    private final RedisRepository redisRepository;

    public ConsumerJobHandler(JsonMapper jsonMapper,
                              ComputeScheduler computeScheduler,
                              RedisKey queue,
                              RedisKey bloomFilter,
                              RedisRepository redisRepository) {
        this.jsonMapper = jsonMapper;
        this.computeScheduler = computeScheduler;
        this.queue = queue;
        this.bloomFilter = bloomFilter;
        this.redisRepository = redisRepository;
    }

    @Override
    public void run(ConsumerJobRequest jobRequest) throws Exception {
        final int limit = jobRequest.limit();
        for (int i = 0; i < limit; i++) {
            try {
                @Language("JSON")
                String json = redisRepository.popQueue(queue);
                if (json != null) {
                    final var payload = jsonMapper.readValue(json, FibonacciComputeRequest.class);
                    final var sequence = payload.sequence();
                    if (redisRepository.checkBloomFilter(bloomFilter, sequence)) {
                        log.info("Skipping: {} - {}", sequence, json);
                        continue;
                    }
                    final var algorithm = payload.algorithm();
                    var uuid = computeScheduler.schedule(new ComputeJobRequest(sequence, algorithm));
                    redisRepository.addBloomFilter(bloomFilter, sequence);
                    log.info("Scheduled: {} - {}", uuid, json);
                }
            } catch (Exception e) {
                log.error("Error while processing queue: {}", e.getMessage(), e);
            }
        }
    }

}

