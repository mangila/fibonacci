package com.github.mangila.fibonacci.web.config;

import com.github.mangila.fibonacci.redis.RedisRepository;
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

    private final RedisRepository redisRepository;

    public FlushConfig(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Bean
    FlywayMigrationStrategy flywayMigrationStrategy() {
        log.info("Flushing redis and postgres");
        redisRepository.flushEverything();
        return flyway -> {
            flyway.clean();
            flyway.migrate();
        };
    }

}
