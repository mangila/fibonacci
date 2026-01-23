package com.github.mangila.fibonacci.scheduler.config;

import com.github.mangila.fibonacci.scheduler.properties.FibonacciProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({FibonacciProperties.class})
public class PropertiesConfig {
}
