package com.github.mangila.fibonacci.shared.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.sse")
@Validated
public class SseProperties {

    private Duration cleanupPeriod = Duration.ofMinutes(10);
    private final Heartbeat heartbeat = new Heartbeat();

    public Duration getCleanupPeriod() {
        return cleanupPeriod;
    }

    public void setCleanupPeriod(Duration cleanupPeriod) {
        this.cleanupPeriod = cleanupPeriod;
    }

    public Heartbeat getHeartbeat() {
        return heartbeat;
    }

    public static class Heartbeat {
        private boolean enabled = false;
        private Duration interval = Duration.ofSeconds(10);

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Duration getInterval() {
            return interval;
        }

        public void setInterval(Duration interval) {
            this.interval = interval;
        }
    }
}
