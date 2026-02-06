package com.github.mangila.fibonacci.web.sse.service;

import com.github.mangila.fibonacci.web.sse.model.SseSession;
import com.github.mangila.fibonacci.web.sse.model.SseSubscription;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Service
public class SseService {

    private final SseSessionRegistry registry;
    private final RedisMessageListenerContainer container;
    private final MessageListenerAdapter adapter;

    public SseService(SseSessionRegistry registry,
                      RedisMessageListenerContainer container,
                      MessageListenerAdapter adapter) {
        this.registry = registry;
        this.container = container;
        this.adapter = adapter;
    }

    public SseEmitter subscribe(SseSubscription subscription) {
        var privateChannel = subscription.channel()
                .concat(":")
                .concat(subscription.username());
        container.addMessageListener(adapter,
                List.of(new ChannelTopic(subscription.channel()),
                        new ChannelTopic(privateChannel))
        );

        var emitter = new SseEmitter(Long.MAX_VALUE);

        registry.add(new SseSession(subscription, emitter));

        return emitter;
    }
}
