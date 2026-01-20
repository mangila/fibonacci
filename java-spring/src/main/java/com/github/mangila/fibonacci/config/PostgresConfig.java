package com.github.mangila.fibonacci.config;

import com.github.mangila.fibonacci.db.PostgresNotificationListener;
import com.github.mangila.fibonacci.event.SpringApplicationPublisher;
import com.zaxxer.hikari.HikariConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class PostgresConfig {

    @Bean
    public SimpleAsyncTaskExecutor postgresListenerExecutor() {
        var executor = new SimpleAsyncTaskExecutor("postgres-notification-listener");
        executor.setVirtualThreads(true);
        return executor;
    }

    @Bean
    public PostgresNotificationListener postgresNotificationListener(HikariConfig hikariConfig,
                                                                     SpringApplicationPublisher publisher,
                                                                     ObjectMapper objectMapper) {
        var dataSource = new SingleConnectionDataSource(
                hikariConfig.getJdbcUrl(),
                hikariConfig.getUsername(),
                hikariConfig.getPassword(),
                false
        );
        dataSource.setAutoCommit(true);
        return new PostgresNotificationListener("livestream", dataSource, publisher, objectMapper);
    }

}
