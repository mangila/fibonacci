package com.github.mangila.fibonacci.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class SpringApplicationPublisher {

    private final ApplicationEventPublisher publisher;

    public SpringApplicationPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishNotification(PgNotificationPayload payload) {
        publisher.publishEvent(payload);
    }
}
