package com.github.mangila.fibonacci.web.sse.model;

import com.github.mangila.fibonacci.core.annotation.AlphaNumeric;
import org.hibernate.validator.constraints.UUID;

public record SseSubscription(
        @AlphaNumeric String channel,
        @UUID String streamKey
) {
}
