package com.github.mangila.fibonacci.web.sse.model;

import com.github.mangila.fibonacci.core.annotation.AlphaNumeric;

public record Subscription(
        @AlphaNumeric String channel,
        @AlphaNumeric String username
) {

    public String privateChannel() {
        return this.channel()
                .concat(":")
                .concat(this.username());
    }

}
