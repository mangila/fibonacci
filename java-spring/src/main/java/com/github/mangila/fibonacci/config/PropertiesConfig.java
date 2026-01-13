package com.github.mangila.fibonacci.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({FibonacciProperties.class})
public class PropertiesConfig {
}
