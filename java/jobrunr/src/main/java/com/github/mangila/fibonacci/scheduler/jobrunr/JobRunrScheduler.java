package com.github.mangila.fibonacci.scheduler.jobrunr;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.core.FibonacciComputeRequest;
import com.github.mangila.fibonacci.redis.RedisConfig;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.jobrunr.scheduling.JobBuilder.aJob;

/**
 * Schedules the Fibonacci computation job using JobRunr.
 */
@Service
public class JobRunrScheduler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(JobRunrScheduler.class);

    private static final List<String> DEFAULT_LABELS = List.of("fibonacci", "compute");

    private volatile boolean running = false;
    private final JsonMapper jsonMapper;
    private final JedisConnectionFactory jedisConnectionFactory;
    private final JobRequestScheduler jobRequestScheduler;

    public JobRunrScheduler(JsonMapper jsonMapper,
                            JedisConnectionFactory jedisConnectionFactory,
                            JobRequestScheduler jobRequestScheduler) {
        this.jsonMapper = jsonMapper;
        this.jedisConnectionFactory = jedisConnectionFactory;
        this.jobRequestScheduler = jobRequestScheduler;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        log.info("Create recurring job");
        jobRequestScheduler.scheduleRecurrently(
                Cron.every15seconds(),
                new InsertRedisZsetJobRequest(50)
        );
    }

    /**
     * Maintains a long-lived connection to Redis and listens for new entries.
     * And then enqueues a new computation JobRun job.
     */
    @Override
    public void run() {
        // redisRepository.pushList(RedisConfig.QUEUE_KEY, jsonMapper.writeValueAsString(new FibonacciComputeRequest(10, FibonacciAlgorithm.ITERATIVE)));
        while (!Thread.currentThread().isInterrupted()) {
            try (Jedis jedis = (Jedis) jedisConnectionFactory.getConnection().getNativeConnection()) {
                while (true) {
                    List<String> pop = jedis.blpop(30, RedisConfig.QUEUE_KEY);
                    if (canEnqueue(pop)) {
                        log.info("Received message: {}", pop);
                        String data = pop.get(1);
                        FibonacciComputeRequest request = jsonMapper.readValue(data, FibonacciComputeRequest.class);
                        final int sequence = request.sequence();
                        final FibonacciAlgorithm algorithm = request.algorithm();
                        log.info("Enqueue job for sequence: {}", sequence);
                        jobRequestScheduler.create(aJob()
                                .scheduleIn(Duration.ofSeconds(1))
                                .withId(UUID.randomUUID())
                                .withName("Fibonacci Calculation for sequence: (%s)")
                                .withAmountOfRetries(3)
                                .withLabels(DEFAULT_LABELS)
                                .withJobRequest(new FibonacciComputeJobRequest(sequence, algorithm)));
                    }
                }
            } catch (Exception e) {
                log.error("Error while processing stream log request", e);
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ignore) {
                    log.warn("Interrupted while sleeping");
                }
            }
        }
    }

    public static boolean canEnqueue(List<String> list) {
        return list != null && list.size() == 2;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
