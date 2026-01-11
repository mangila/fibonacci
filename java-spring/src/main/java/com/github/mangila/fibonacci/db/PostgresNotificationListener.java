package com.github.mangila.fibonacci.db;

import com.github.mangila.fibonacci.event.SpringApplicationPublisher;
import org.postgresql.PGNotification;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgresNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(PostgresNotificationListener.class);

    private volatile boolean running = false;
    private final String channel;
    private final SingleConnectionDataSource dataSource;
    private final SpringApplicationPublisher publisher;

    public PostgresNotificationListener(String channel,
                                        SingleConnectionDataSource dataSource,
                                        SpringApplicationPublisher publisher) {
        this.channel = channel;
        this.dataSource = dataSource;
        this.publisher = publisher;
    }

    public void start() {
        running = true;
        while (running && !Thread.currentThread().isInterrupted()) {
            // Listens for notifications; reconnects after connection errors
            try (Connection connection = dataSource.getConnection()) {
                PgConnection pgConnection = connection.unwrap(PgConnection.class);
                try (Statement stmt = pgConnection.createStatement()) {
                    stmt.execute("LISTEN %s".formatted(channel));
                }
                while (running && !Thread.currentThread().isInterrupted()) {
                    try {
                        PGNotification[] pgNotifications = pgConnection.getNotifications(0);
                        publisher.publishNotification(pgNotifications);
                    } catch (SQLException e) {
                        log.error("Error while listening for notifications", e);
                        break;
                    }
                }
            } catch (SQLException e) {
                log.error("Error while getting connection - will try to reconnect in 5s", e);
                try {
                    Thread.sleep(5000);
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
