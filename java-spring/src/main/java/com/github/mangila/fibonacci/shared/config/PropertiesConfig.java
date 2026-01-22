package com.github.mangila.fibonacci.shared.config;

import com.github.mangila.fibonacci.shared.properties.FibonacciProperties;
import com.github.mangila.fibonacci.shared.properties.LivestreamProperties;
import com.github.mangila.fibonacci.shared.properties.SseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        FibonacciProperties.class,
        LivestreamProperties.class,
        SseProperties.class})
public class PropertiesConfig {
}
