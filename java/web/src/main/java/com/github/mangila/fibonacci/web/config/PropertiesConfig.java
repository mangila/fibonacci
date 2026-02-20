package com.github.mangila.fibonacci.web.config;

import com.github.mangila.fibonacci.web.properties.PgListenProperties;
import com.github.mangila.fibonacci.web.sse.properties.SseProperties;
import com.github.mangila.fibonacci.web.ws.properties.WsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        SseProperties.class,
        WsProperties.class,
        PgListenProperties.class})
public class PropertiesConfig {
}
