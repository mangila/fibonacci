package com.github.mangila.fibonacci.web.config;

import com.github.mangila.fibonacci.web.shared.PostgresNotificationListener;
import com.zaxxer.hikari.HikariConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class PostgresListenerConfig {

    @Bean
    PostgresNotificationListener postgresNotificationListener(
            JsonMapper jsonMapper,
            ApplicationEventPublisher publisher,
            SimpleAsyncTaskExecutor postgresListenerExecutor,
            HikariConfig hikariConfig
    ) {

        return new PostgresNotificationListener(jsonMapper, publisher, postgresListenerExecutor, hikariConfig);
    }

}
