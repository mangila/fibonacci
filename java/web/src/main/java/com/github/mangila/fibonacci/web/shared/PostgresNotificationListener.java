package com.github.mangila.fibonacci.web.shared;

import com.github.mangila.fibonacci.postgres.FibonacciProjection;
import com.zaxxer.hikari.HikariConfig;
import io.github.mangila.ensure4j.Ensure;
import org.intellij.lang.annotations.Language;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class PostgresNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(PostgresNotificationListener.class);

    private final JsonMapper jsonMapper;
    private final ApplicationEventPublisher publisher;
    private final SimpleAsyncTaskExecutor postgresListenerExecutor;
    private final SingleConnectionDataSource dataSource;

    public PostgresNotificationListener(JsonMapper jsonMapper,
                                        ApplicationEventPublisher publisher,
                                        SimpleAsyncTaskExecutor postgresListenerExecutor,
                                        HikariConfig hikariConfig) {
        this.jsonMapper = jsonMapper;
        this.publisher = publisher;
        this.postgresListenerExecutor = postgresListenerExecutor;
        this.dataSource = new SingleConnectionDataSource(
                hikariConfig.getJdbcUrl(),
                hikariConfig.getUsername(),
                hikariConfig.getPassword(),
                false);
    }

    @EventListener(ApplicationReadyEvent.class)
    void listen() {
        log.info("Postgres notification listener is enabled");
        postgresListenerExecutor.execute(() -> {
            final int blockingDuration = Math.toIntExact(Duration.ofSeconds(30).toMillis());
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    var connection = dataSource.getConnection();
                    @Language("PostgreSQL") final var sql = "LISTEN fibonacci;";
                    try (var stmt = connection.prepareStatement(sql)) {
                        stmt.execute();
                    }
                    log.info("Listening for notifications - {}", sql);
                    while (!Thread.currentThread().isInterrupted()) {
                        var pgConnection = connection.unwrap(PGConnection.class);
                        var notifications = pgConnection.getNotifications(blockingDuration);
                        Ensure.notNull(notifications, "Notifications are null");
                        for (PGNotification notification : notifications) {
                            final String payload = notification.getParameter();
                            var projection = jsonMapper.readValue(payload, FibonacciProjection.class);
                            publisher.publishEvent(projection);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error while listening for notifications: {}", e.getMessage(), e);
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

}
