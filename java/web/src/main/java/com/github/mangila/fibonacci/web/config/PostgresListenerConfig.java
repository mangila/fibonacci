package com.github.mangila.fibonacci.web.config;

import com.github.mangila.fibonacci.web.properties.PgListenProperties;
import com.github.mangila.fibonacci.web.shared.PostgresNotificationListener;
import com.zaxxer.hikari.HikariConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import tools.jackson.databind.json.JsonMapper;

@ConditionalOnProperty(prefix = "app.pg-listen", name = "enabled", havingValue = "true")
@Configuration
public class PostgresListenerConfig {

    private static final Logger log = LoggerFactory.getLogger(PostgresListenerConfig.class);

    @Bean
    PostgresNotificationListener postgresNotificationListener(
            PgListenProperties pgListenProperties,
            JsonMapper jsonMapper,
            ApplicationEventPublisher publisher,
            SimpleAsyncTaskExecutor postgresListenerExecutor,
            HikariConfig hikariConfig
    ) {
        log.info("Postgres notification listener is enabled");
        return new PostgresNotificationListener(pgListenProperties, jsonMapper, publisher, postgresListenerExecutor, hikariConfig);
    }

}
