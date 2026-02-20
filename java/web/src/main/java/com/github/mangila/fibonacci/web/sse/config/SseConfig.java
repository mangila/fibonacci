package com.github.mangila.fibonacci.web.sse.config;

import com.github.mangila.fibonacci.web.sse.properties.SseProperties;
import com.github.mangila.fibonacci.web.sse.service.SseLivestreamListener;
import com.github.mangila.fibonacci.web.sse.service.SseScheduler;
import com.github.mangila.fibonacci.web.sse.service.SseSessionRegistry;
import com.github.mangila.fibonacci.web.sse.service.SseSubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

@ConditionalOnProperty(prefix = "app.sse", name = "enabled", havingValue = "true")
@Configuration
public class SseConfig {

    private static final Logger log = LoggerFactory.getLogger(SseConfig.class);

    @Bean("sseTaskScheduler")
    SimpleAsyncTaskScheduler sseTaskScheduler() {
        var scheduler = new SimpleAsyncTaskScheduler();
        scheduler.setThreadNamePrefix("sse-scheduler-");
        scheduler.setVirtualThreads(true);
        return scheduler;
    }

    @Bean
    SseSessionRegistry sseSessionRegistry() {
        return new SseSessionRegistry();
    }

    @Bean
    SseLivestreamListener sseLivestreamListener(SseSessionRegistry registry) {
        return new SseLivestreamListener(registry);
    }

    @Bean
    SseSubscriptionService sseSubscriptionService(SseSessionRegistry registry) {
        return new SseSubscriptionService(registry);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.sse", name = "heartbeat.enabled", havingValue = "true")
    SseScheduler sseHeartbeatScheduler(
            SseProperties sseProperties,
            SimpleAsyncTaskScheduler sseTaskScheduler,
            SseSessionRegistry registry
    ) {
        log.info("SSE heartbeat enabled");
        return new SseScheduler(
                sseProperties,
                sseTaskScheduler,
                registry
        );
    }
}
