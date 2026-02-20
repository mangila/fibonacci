package com.github.mangila.fibonacci.web.sse.service;

import com.github.mangila.fibonacci.web.shared.RedisPublisher;
import com.github.mangila.fibonacci.web.sse.model.SseIdQuery;
import com.github.mangila.fibonacci.web.sse.model.SseSession;
import com.github.mangila.fibonacci.web.sse.model.SseStreamQuery;
import com.github.mangila.fibonacci.web.sse.model.SseSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Service
public class SseSubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SseSubscriptionService.class);

    private final RedisPublisher publisher;
    private final SseSessionRegistry registry;
    private final RedisMessageListenerContainer container;
    private final MessageListenerAdapter adapter;

    public SseSubscriptionService(RedisPublisher publisher,
                                  SseSessionRegistry registry,
                                  RedisMessageListenerContainer container,
                                  @Qualifier("sseListenerAdapter") MessageListenerAdapter adapter) {
        this.publisher = publisher;
        this.registry = registry;
        this.container = container;
        this.adapter = adapter;
    }

    public SseEmitter subscribe(SseSubscription sseSubscription) {
        var topics = List.of(new ChannelTopic(sseSubscription.channel()));
        log.info("{} - Subscribing to topics: {}", sseSubscription.username(), topics);
        container.addMessageListener(adapter, topics);
        var emitter = new SseEmitter(Long.MAX_VALUE);
        registry.add(new SseSession(sseSubscription, emitter));
        return emitter;
    }

    public void queryByStream(SseStreamQuery streamQuery) {
        final var option = streamQuery.option();
        final var subscription = streamQuery.sseSubscription();
        publisher.publish(subscription.channel(), option);
    }

    public void queryById(SseIdQuery idQuery) {
        final var option = idQuery.option();
        final var subscription = idQuery.sseSubscription();
        publisher.publish(subscription.channel(), option);
    }
}
