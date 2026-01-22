package com.github.mangila.fibonacci.db;

import com.github.mangila.fibonacci.db.model.PgNotificationPayload;
import com.github.mangila.fibonacci.db.model.PgNotificationPayloadCollection;
import com.github.mangila.fibonacci.shared.SpringApplicationPublisher;
import io.github.mangila.ensure4j.Ensure;
import org.postgresql.PGNotification;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import tools.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PostgresNotificationListener implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(PostgresNotificationListener.class);

    private volatile boolean running = false;

    private final String channel;
    private final SingleConnectionDataSource dataSource;
    private final SpringApplicationPublisher publisher;
    private final ObjectMapper objectMapper;

    public PostgresNotificationListener(String channel,
                                        SingleConnectionDataSource dataSource,
                                        SpringApplicationPublisher publisher,
                                        ObjectMapper objectMapper) {
        this.channel = channel;
        this.dataSource = dataSource;
        this.publisher = publisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run() {
        Ensure.notBlank(channel, "Channel cannot be blank");
        running = true;
        while (running && !Thread.currentThread().isInterrupted()) {
            // Listens for notifications; reconnects after connection errors
            try (Connection connection = dataSource.getConnection()) {
                PgConnection pgConnection = connection.unwrap(PgConnection.class);
                try (Statement stmt = pgConnection.createStatement()) {
                    // LISTEN does not support '?' placeholders.
                    // Ensure channel is a valid identifier.
                    stmt.execute("LISTEN %s".formatted(channel));
                }
                log.info("Listening for notifications on channel: '{}'", channel);
                while (running && !Thread.currentThread().isInterrupted()) {
                    try {
                        PGNotification[] pgNotifications = pgConnection.getNotifications(0);
                        List<PgNotificationPayload> notifications = Arrays.stream(pgNotifications)
                                .map(PGNotification::getParameter)
                                .map(json -> objectMapper.readValue(json, PgNotificationPayload.class))
                                .toList();
                        publisher.publishNotification(new PgNotificationPayloadCollection(notifications));
                    } catch (SQLException e) {
                        log.error("Error while listening for notifications", e);
                        break;
                    }
                }
            } catch (SQLException e) {
                log.error("Error while getting connection - will try to reconnect in 5s", e);
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
