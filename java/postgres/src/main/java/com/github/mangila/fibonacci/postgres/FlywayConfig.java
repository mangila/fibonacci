package com.github.mangila.fibonacci.postgres;

import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class FlywayConfig {

    @Bean
    @Profile("dev")
    public FlywayMigrationStrategy flywayMigrationStrategyDev() {
        return flyway -> {
            // VERY dangerous to even have this instruction compiled, but in this backend it's ok
            flyway.clean();
            flyway.migrate();
        };
    }
}
