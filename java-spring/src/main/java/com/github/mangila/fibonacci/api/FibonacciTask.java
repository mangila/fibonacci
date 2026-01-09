package com.github.mangila.fibonacci.api;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.mangila.fibonacci.model.FibonacciOption;
import com.github.mangila.fibonacci.model.FibonacciState;
import io.github.mangila.ensure4j.Ensure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;

import java.math.BigInteger;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public record FibonacciTask(SimpMessagingTemplate template,
                            String destination,
                            FibonacciOption option) implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(FibonacciTask.class);

    private static final Cache<Integer, FibonacciState> FIBONACCI_STATE_CACHE = Caffeine.newBuilder()
            .maximumSize(100_000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    private static final Cache<Integer, Message<byte[]>> FIBONACCI_MESSAGE_CACHE = Caffeine.newBuilder()
            .maximumSize(100_000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    @Override
    public void run() {
        final int limit = option.limit();
        final int offset = option.offset();
        log.info("Starting FibonacciTask to destination {} with offset {}", destination, offset);
        FibonacciState state = Ensure.notNullOrElseGet(FIBONACCI_STATE_CACHE.getIfPresent(offset),
                () -> {
                    var newState = FibonacciGenerator.generate(offset);
                    FIBONACCI_STATE_CACHE.put(offset, newState);
                    return newState;
                });
        int nextOffset = offset + 2;
        BigInteger previous = state.previous();
        BigInteger current = state.current();
        sendBinary(previous, offset);
        sendBinary(current, nextOffset - 1);
        for (int i = 2; i < limit; i++) {
            state = FIBONACCI_STATE_CACHE.getIfPresent(nextOffset);
            if (state == null) {
                var next = previous.add(current);
                previous = current;
                current = next;
                FIBONACCI_STATE_CACHE.put(nextOffset, new FibonacciState(previous, current));
            } else {
                previous = state.previous();
                current = state.current();
            }
            sendBinary(current, nextOffset);
            nextOffset++;
            // relax a lil bit after 100 iterations
            if (i % 100 == 0) {
                delay(Duration.ofMillis(ThreadLocalRandom.current().nextInt(1, 6)));
            }
        }
        log.info("Finished FibonacciTask to destination {} - {}", destination, nextOffset);
    }

    private void sendBinary(BigInteger value, int currentOffset) {
        Message<byte[]> message = Ensure.notNullOrElseGet(FIBONACCI_MESSAGE_CACHE.getIfPresent(currentOffset), () -> {
            SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            accessor.setDestination(destination);
            accessor.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            // TODO: add offset in header or in the payload? String.valueOf is maybe not the best choice in tight loops
            accessor.setNativeHeader("offset", String.valueOf(currentOffset));
            Message<byte[]> msg = MessageBuilder.createMessage(value.toByteArray(), accessor.getMessageHeaders());
            FIBONACCI_MESSAGE_CACHE.put(currentOffset, msg);
            return msg;
        });
        template.send(message);
    }

    private void delay(Duration duration) {
        try {
            TimeUnit.MILLISECONDS.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}