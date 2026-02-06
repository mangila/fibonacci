package com.github.mangila.fibonacci.web.sse.config;

import com.github.mangila.fibonacci.web.sse.model.SseQuery;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;

@Configuration
public class SseConfig {

    @Bean
    MessageListenerAdapter listenerAdapter(SseMessageHandler sseMessageHandler) {
        var adapter = new MessageListenerAdapter(sseMessageHandler, "handleMessage");
        adapter.setSerializer(new JacksonJsonRedisSerializer<>(SseQuery.class));
        return adapter;
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(JedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

}
