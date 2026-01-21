package com.github.mangila.fibonacci.event;

import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.EnsureException;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class SpringApplicationPublisher {

    private static final Logger log = LoggerFactory.getLogger(SpringApplicationPublisher.class);
    private final ApplicationEventPublisher publisher;

    public SpringApplicationPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Retryable(
            includes = {Exception.class},
            maxRetries = 4,
            delayString = "500ms",
            multiplier = 1.5,
            maxDelay = 3000
    )
    @ConcurrencyLimit(limit = 1)
    public void publishNotification(@NonNull FibonacciProjectionList payload) {
        try {
            Ensure.notNull(payload, "Payload cannot be null");
            publisher.publishEvent(payload);
        } catch (EnsureException e) {
            log.error(e.getMessage(), e);
        }
    }
}
