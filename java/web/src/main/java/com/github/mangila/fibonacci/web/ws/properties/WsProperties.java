package com.github.mangila.fibonacci.web.ws.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.ws")
@Validated
public class WsProperties {
}
