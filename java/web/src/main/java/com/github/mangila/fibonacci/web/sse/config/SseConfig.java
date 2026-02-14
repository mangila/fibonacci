package com.github.mangila.fibonacci.web.sse.config;

import com.github.mangila.fibonacci.web.sse.service.SseRedisMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

import java.nio.charset.StandardCharsets;

@Configuration
public class SseConfig {

    @Bean("sseTaskScheduler")
    SimpleAsyncTaskScheduler sseTaskScheduler() {
        var scheduler = new SimpleAsyncTaskScheduler();
        scheduler.setThreadNamePrefix("sse-scheduler-");
        scheduler.setVirtualThreads(true);
        return scheduler;
    }

    @Bean("sseListenerAdapter")
    MessageListenerAdapter sseListenerAdapter(SseRedisMessageHandler sseRedisMessageHandler) {
        var adapter = new MessageListenerAdapter(sseRedisMessageHandler, "handleSseMessage");
        adapter.setSerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
        return adapter;
    }
}
