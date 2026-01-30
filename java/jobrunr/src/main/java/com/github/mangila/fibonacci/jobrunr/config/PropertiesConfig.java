package com.github.mangila.fibonacci.jobrunr.config;

import com.github.mangila.fibonacci.jobrunr.properties.ApplicationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ApplicationProperties.class})
public class PropertiesConfig {
}
