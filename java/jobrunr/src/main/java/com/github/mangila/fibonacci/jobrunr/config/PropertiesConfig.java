package com.github.mangila.fibonacci.jobrunr.config;

import com.github.mangila.fibonacci.jobrunr.job.consumer.ConsumerProperties;
import com.github.mangila.fibonacci.jobrunr.job.producer.ProducerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        ConsumerProperties.class,
        ProducerProperties.class})
public class PropertiesConfig {
}
