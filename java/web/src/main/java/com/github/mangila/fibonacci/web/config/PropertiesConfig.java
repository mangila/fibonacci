package com.github.mangila.fibonacci.web.config;

import com.github.mangila.fibonacci.web.properties.LivestreamProperties;
import com.github.mangila.fibonacci.web.properties.SseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        LivestreamProperties.class,
        SseProperties.class})
public class PropertiesConfig {
}
