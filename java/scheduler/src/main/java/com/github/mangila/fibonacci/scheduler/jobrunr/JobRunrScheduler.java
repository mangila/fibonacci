package com.github.mangila.fibonacci.scheduler.jobrunr;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.jobrunr.scheduling.JobBuilder.aJob;

@Service
public class JobRunrScheduler implements Runnable {

    private static final Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(JobRunrScheduler.class));

    private final static String DEFAULT_NAME = "Fibonacci Calculation for sequence: (%s)";
    private final static int DEFAULT_RETIRES = 3;
    private final static List<String> DEFAULT_LABELS = List.of("fibonacci", "compute");

    private volatile boolean running = false;
    private final StringRedisTemplate stringRedisTemplate;
    private final JobRequestScheduler jobRequestScheduler;

    public JobRunrScheduler(StringRedisTemplate stringRedisTemplate,
                            JobRequestScheduler jobRequestScheduler) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.jobRequestScheduler = jobRequestScheduler;
    }

    @Override
    public void run() {
        running = true;
        final int sequence = 1;
        final FibonacciAlgorithm algorithm = FibonacciAlgorithm.RECURSIVE;
        jobRequestScheduler.create(aJob()
                .withId(UUID.randomUUID())
                .withName(DEFAULT_NAME.formatted(sequence))
                .withAmountOfRetries(DEFAULT_RETIRES)
                .withLabels(DEFAULT_LABELS)
                .withJobRequest(new FibonacciComputeJobRequest(sequence, algorithm)));
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
