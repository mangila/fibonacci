package com.github.mangila.fibonacci.web.sse.config;

import com.github.mangila.fibonacci.web.sse.service.SseMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

import java.nio.charset.StandardCharsets;

@Configuration
public class SseConfig {

    private static final Logger log = LoggerFactory.getLogger(SseConfig.class);

    @Bean
    MessageListenerAdapter listenerAdapter(SseMessageHandler sseMessageHandler) {
        var adapter = new MessageListenerAdapter(sseMessageHandler, "handleMessage");
        adapter.setSerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
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

    @Bean("sseTaskScheduler")
    SimpleAsyncTaskScheduler sseTaskScheduler() {
        var scheduler = new SimpleAsyncTaskScheduler();
        scheduler.setThreadNamePrefix("sse-scheduler-");
        scheduler.setVirtualThreads(true);
        return scheduler;
    }

}
