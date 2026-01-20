package com.github.mangila.fibonacci.event;

import io.github.mangila.ensure4j.Ensure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class SpringApplicationPublisher {

    private static final Logger log = LoggerFactory.getLogger(SpringApplicationPublisher.class);
    private final ApplicationEventPublisher publisher;

    public SpringApplicationPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishNotification(PgNotificationPayload payload) {
        try {
            Ensure.notNull(payload, "Payload cannot be null");
            publisher.publishEvent(payload);
        } catch (Exception e) {
            log.error("Failed to publish event - {}", payload, e);
        }
    }
}
