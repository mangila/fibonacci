package com.github.mangila.fibonacci.scheduler.jobrunr;

import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class JobRunrScheduler implements Runnable {

    private static final Logger log = new JobRunrDashboardLogger(LoggerFactory.getLogger(JobRunrScheduler.class));

    private volatile boolean running = false;
    private final StringRedisTemplate stringRedisTemplate;
    private final JobScheduler jobScheduler;

    public JobRunrScheduler(StringRedisTemplate stringRedisTemplate,
                            JobScheduler jobScheduler) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.jobScheduler = jobScheduler;
    }

    @Override
    public void run() {
        running = true;
        var params = stringRedisTemplate.opsForList().leftPop("queue", Duration.ZERO);
        System.out.println("hej");
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    /*public void scheduleFibonacciCalculations(FibonacciComputeCommand command) {
        Ensure.notNull(command);
        final int start = command.start();
        final int end = command.end();
        final FibonacciAlgorithm algorithm = command.algorithm();
        Ensure.isTrue(computeProperties.getMax() >= end, "End sequence must be within the configured max limit");
        Stream<Integer> sequenceStream = IntStream.range(start, end)
                // this could cause a race condition, but it's ok
                .filter(sequenceCache::tryCompute)
                .peek(sequence -> log.info("Scheduling Fibonacci computation for sequence {}", sequence))
                .boxed();
        jobScheduler.enqueue(sequenceStream, (sequence) -> computeFibonacci(algorithm, sequence));
    }*/
}
