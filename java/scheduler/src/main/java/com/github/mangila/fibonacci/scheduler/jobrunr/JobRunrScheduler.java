package com.github.mangila.fibonacci.scheduler.jobrunr;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import com.github.mangila.fibonacci.redis.RedisRepository;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.jobrunr.scheduling.JobBuilder.aJob;

/**
 * Schedules the Fibonacci computation job using JobRunr.
 */
@Service
public class JobRunrScheduler implements Runnable {

    private static final Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(JobRunrScheduler.class));

    private final static List<String> DEFAULT_LABELS = List.of("fibonacci", "compute");

    private volatile boolean running = false;

    private final JobRequestScheduler jobRequestScheduler;
    private final RedisRepository redisRepository;

    public JobRunrScheduler(JobRequestScheduler jobRequestScheduler,
                            RedisRepository redisRepository) {
        this.jobRequestScheduler = jobRequestScheduler;
        this.redisRepository = redisRepository;
    }

    /**
     * Event listener for the ApplicationReadyEvent.
     * Creates a recurring job to compute Fibonacci numbers and try to reserve a redis bloom filter.
     */
    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Create recurring job");
        jobRequestScheduler.scheduleRecurrently(
                Cron.every15seconds(),
                new InsertRedisStreamJobRequest(50)
        );
        log.info("Try reserve bloom filter");
        redisRepository.tryReserveBloomFilter();
    }

    /**
     * Block until an entry is available in the redis queue
     * and then enqueues a new computation JobRun job.
     */
    @Override
    public void run() {
        running = true;
        redisRepository.longBlockingOperation(jedis -> {
            //  var params = jedis.blpop(30, RedisConfig.SEQUENCE_QUEUE_KEY);
            final int sequence = 1;
            final FibonacciAlgorithm algorithm = FibonacciAlgorithm.RECURSIVE;
            jobRequestScheduler.create(aJob()
                    .withId(UUID.randomUUID())
                    .withName("Fibonacci Calculation for sequence: (%s)")
                    .withAmountOfRetries(3)
                    .withLabels(DEFAULT_LABELS)
                    .withJobRequest(new FibonacciComputeJobRequest(sequence, algorithm)));
        });
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
