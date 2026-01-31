package com.github.mangila.fibonacci.jobrunr.config;

import com.github.mangila.fibonacci.jobrunr.job.consumer.ConsumerProperties;
import com.github.mangila.fibonacci.jobrunr.job.producer.ProducerProperties;
import com.github.mangila.fibonacci.jobrunr.job.zset.drain.DrainZsetProperties;
import com.github.mangila.fibonacci.jobrunr.job.zset.insert.InsertZsetProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        ConsumerProperties.class,
        ProducerProperties.class,
        InsertZsetProperties.class,
        DrainZsetProperties.class})
public class PropertiesConfig {
}
