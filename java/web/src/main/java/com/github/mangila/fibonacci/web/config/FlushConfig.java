package com.github.mangila.fibonacci.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(prefix = "app.flush", name = "enabled", havingValue = "true")
@Configuration
public class FlushConfig {

    private static final Logger log = LoggerFactory.getLogger(FlushConfig.class);

    @Bean
    FlywayMigrationStrategy flywayMigrationStrategy() {
        log.info("Flushing postgres");
        return flyway -> {
            flyway.clean();
            flyway.migrate();
        };
    }

}
