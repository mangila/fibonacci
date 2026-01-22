package com.github.mangila.fibonacci.shared;

import com.github.mangila.fibonacci.db.model.PgNotificationPayloadCollection;
import io.github.mangila.ensure4j.Ensure;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class SpringApplicationPublisher {

    private final ApplicationEventPublisher publisher;

    public SpringApplicationPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishNotification(@NonNull PgNotificationPayloadCollection payload) {
        Ensure.notNull(payload, "Payload cannot be null");
        publisher.publishEvent(payload);
    }
}
