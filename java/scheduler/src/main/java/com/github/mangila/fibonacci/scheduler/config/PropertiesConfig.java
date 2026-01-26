package com.github.mangila.fibonacci.scheduler.config;

import com.github.mangila.fibonacci.scheduler.properties.ComputeProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(value = {
        ComputeProperties.class
})
@Configuration
public class PropertiesConfig {
}
