package com.github.mangila.fibonacci.web.sse.model;

import io.github.mangila.ensure4j.Ensure;

import java.util.concurrent.ConcurrentHashMap;

public record SseContext(
        String channel,
        String streamKey,
        ConcurrentHashMap<String, Object> properties
) {

    public SseContext {
        Ensure.notNull(channel);
        Ensure.notNull(streamKey);
    }

    public static SseContext from(String channel, String streamKey) {
        return new SseContext(channel, streamKey, new ConcurrentHashMap<>());
    }
}
