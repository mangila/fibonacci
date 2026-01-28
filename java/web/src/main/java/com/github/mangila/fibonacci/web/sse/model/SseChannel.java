package com.github.mangila.fibonacci.web.sse.model;

import org.jspecify.annotations.Nullable;

import java.util.concurrent.CopyOnWriteArraySet;

public record SseChannel(SseChannelName name,
                         CopyOnWriteArraySet<SseSession> sessions) {


    @Nullable
    public SseSession getSession(String streamKey) {
        return sessions.stream()
                .filter(session -> {
                    return session.context().streamKey().equals(streamKey);
                })
                .findFirst()
                .orElse(null);
    }

    public int size() {
        return sessions.size();
    }

    public void completeAndClear() {
        sessions.forEach(SseSession::complete);
        sessions.clear();
    }

}