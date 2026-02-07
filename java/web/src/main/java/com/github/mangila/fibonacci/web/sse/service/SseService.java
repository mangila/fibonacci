package com.github.mangila.fibonacci.web.sse.service;

import com.github.mangila.fibonacci.web.sse.model.Request;
import com.github.mangila.fibonacci.web.sse.model.Session;
import com.github.mangila.fibonacci.web.sse.model.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Service
public class SseService {

    private static final Logger log = LoggerFactory.getLogger(SseService.class);

    private final SseRedisPublisher publisher;
    private final SseSessionRegistry registry;
    private final RedisMessageListenerContainer container;
    private final MessageListenerAdapter adapter;

    public SseService(SseRedisPublisher publisher,
                      SseSessionRegistry registry,
                      RedisMessageListenerContainer container,
                      MessageListenerAdapter adapter) {
        this.publisher = publisher;
        this.registry = registry;
        this.container = container;
        this.adapter = adapter;
    }

    public SseEmitter subscribe(Subscription subscription) {
        var topics = List.of(
                new ChannelTopic(subscription.channel()),
                new ChannelTopic(subscription.privateChannel())
        );
        log.info("{} - Subscribing to topics: {}", subscription.username(), topics);
        container.addMessageListener(adapter, topics);

        var emitter = new SseEmitter(Long.MAX_VALUE);

        registry.add(new Session(subscription, emitter));

        return emitter;
    }

    public void query(Request request) {
        log.info("Querying for {}", request);
        final var query = request.option();
        final var subscription = request.subscription();
        publisher.publish(subscription.channel(), query);
        publisher.publish(subscription.privateChannel(), query);
    }
}
