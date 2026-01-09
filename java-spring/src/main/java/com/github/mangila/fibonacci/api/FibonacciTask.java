package com.github.mangila.fibonacci.api;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.mangila.fibonacci.model.FibonacciOption;
import com.github.mangila.fibonacci.model.FibonacciState;
import io.github.mangila.ensure4j.Ensure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public record FibonacciTask(SimpMessagingTemplate template, java.security.Principal principal,
                            FibonacciOption option) implements Runnable {

    private static final Cache<Integer, FibonacciState> FIBONACCI_STATE_CACHE = Caffeine.newBuilder()
            .maximumSize(100_000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();
    private static final Logger log = LoggerFactory.getLogger(FibonacciTask.class);

    @Override
    public void run() {
        log.info("Starting FibonacciTask for user {} with offset {}", principal.getName(), option.offset());
        FibonacciState state = Ensure.notNullOrElseGet(FIBONACCI_STATE_CACHE.getIfPresent(option.offset()),
                () -> {
                    var newState = FibonacciGenerator.generate(option.offset());
                    FIBONACCI_STATE_CACHE.put(option.offset(), newState);
                    return newState;
                });
        int offset = option.offset();
        final int limit = option.limit();
        offset += 2;
        BigInteger previous = state.previous();
        BigInteger current = state.current();
        sendBinary(previous, offset);
        sendBinary(current, offset);
        for (int i = 2; i < limit; i++) {
            state = FIBONACCI_STATE_CACHE.getIfPresent(offset);
            if (state == null) {
                var next = previous.add(current);
                previous = current;
                current = next;
                FIBONACCI_STATE_CACHE.put(offset, new FibonacciState(previous, current));
            } else {
                previous = state.previous();
                current = state.current();
            }
            sendBinary(current, offset);
            offset++;
        }
    }

    private void sendBinary(BigInteger value, int currentOffset) {
        template.convertAndSendToUser(
                principal.getName(),
                "/queue/results",
                // big-endian
                value.toByteArray(),
                Map.of("offset", currentOffset)
        );
    }
}