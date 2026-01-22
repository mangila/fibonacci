package com.github.mangila.fibonacci.db;

import com.github.mangila.fibonacci.shared.SpringApplicationPublisher;
import com.github.mangila.fibonacci.shared.properties.LivestreamProperties;
import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class PostgresConfig {

    @Bean
    @ConditionalOnProperty(
            value = "app.livestream.enabled",
            havingValue = "true"
    )
    PostgresNotificationListener postgresNotificationListener(HikariConfig hikariConfig,
                                                              SpringApplicationPublisher publisher,
                                                              LivestreamProperties livestreamProperties,
                                                              ObjectMapper objectMapper) {
        var dataSource = new SingleConnectionDataSource(
                hikariConfig.getJdbcUrl(),
                hikariConfig.getUsername(),
                hikariConfig.getPassword(),
                false
        );
        dataSource.setAutoCommit(true);
        return new PostgresNotificationListener(livestreamProperties.getPgChannel(), dataSource, publisher, objectMapper);
    }

    @Bean
    @ConditionalOnProperty(
            value = "app.livestream.enabled",
            havingValue = "true"
    )
    PostgresNotificationWatcher postgresNotificationWatcher(SimpleAsyncTaskExecutor postgresListenerExecutor,
                                                            PostgresNotificationListener listener) {
        return new PostgresNotificationWatcher(postgresListenerExecutor, listener);
    }

}
