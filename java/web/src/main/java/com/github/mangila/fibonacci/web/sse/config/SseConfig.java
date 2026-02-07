package com.github.mangila.fibonacci.web.sse.config;

import com.github.mangila.fibonacci.web.sse.model.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;

@Configuration
public class SseConfig {

    private static final Logger log = LoggerFactory.getLogger(SseConfig.class);

    @Bean
    MessageListenerAdapter listenerAdapter(SseMessageHandler sseMessageHandler) {
        var adapter = new MessageListenerAdapter(sseMessageHandler, "handleMessage");
        adapter.setSerializer(new JacksonJsonRedisSerializer<>(Query.class));
        return adapter;
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(
            SimpleAsyncTaskExecutor ioAsyncTaskExecutor,
            JedisConnectionFactory connectionFactory
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setTaskExecutor(ioAsyncTaskExecutor);
        container.setErrorHandler(t -> {
            log.error("Error handling Redis message", t);
        });
        return container;
    }

}
