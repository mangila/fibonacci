package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.shared.properties.FibonacciProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class FibonacciScheduler {

    private static final Logger log = LoggerFactory.getLogger(FibonacciScheduler.class);

    private final SimpleAsyncTaskScheduler fibonacciTaskScheduler;
    private final FibonacciProperties fibonacciProperties;
    private final FibonacciTask fibonacciTask;

    public FibonacciScheduler(SimpleAsyncTaskScheduler fibonacciTaskScheduler,
                              FibonacciProperties fibonacciProperties,
                              FibonacciTask fibonacciTask) {
        this.fibonacciTaskScheduler = fibonacciTaskScheduler;
        this.fibonacciProperties = fibonacciProperties;
        this.fibonacciTask = fibonacciTask;
    }

    @EventListener(ApplicationReadyEvent.class)
    void init() {
        fibonacciTaskScheduler.scheduleWithFixedDelay(() -> {
            if (fibonacciTask.isLimitReached()) {
                log.info("Fibonacci computation limit reached, closing scheduler");
                fibonacciTaskScheduler.close();
                return;
            }
            fibonacciTask.run();
        }, fibonacciProperties.getDelay());
    }
}
