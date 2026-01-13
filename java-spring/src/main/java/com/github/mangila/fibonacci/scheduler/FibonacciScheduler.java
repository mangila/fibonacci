package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.config.FibonacciProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class FibonacciScheduler {

    private static final Logger log = LoggerFactory.getLogger(FibonacciScheduler.class);

    private final SimpleAsyncTaskScheduler simpleAsyncTaskScheduler;
    private final FibonacciProperties fibonacciProperties;
    private final FibonacciTask fibonacciTask;

    public FibonacciScheduler(@Qualifier("simpleAsyncTaskScheduler") SimpleAsyncTaskScheduler simpleAsyncTaskScheduler,
                              FibonacciProperties fibonacciProperties,
                              FibonacciTask fibonacciTask) {
        this.simpleAsyncTaskScheduler = simpleAsyncTaskScheduler;
        this.fibonacciProperties = fibonacciProperties;
        this.fibonacciTask = fibonacciTask;
    }

    @EventListener(ApplicationReadyEvent.class)
    void init() {
        simpleAsyncTaskScheduler.scheduleWithFixedDelay(() -> {
            if (fibonacciTask.isLimitReached()) {
                log.info("Fibonacci computation limit reached, closing scheduler");
                simpleAsyncTaskScheduler.close();
                return;
            }
            fibonacciTask.run();
        }, fibonacciProperties.getDelay());
    }
}
