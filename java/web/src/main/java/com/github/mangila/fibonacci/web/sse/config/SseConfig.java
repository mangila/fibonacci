package com.github.mangila.fibonacci.web.sse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class SseConfig {

    private final ObjectMapper objectMapper;

    public SseConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(SseMessageHandler sseMessageHandler) {
        var adapter = new MessageListenerAdapter(sseMessageHandler, "handleMessage");
        adapter.setSerializer(new GenericJacksonJsonRedisSerializer(objectMapper));
        return adapter;
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(JedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

}
