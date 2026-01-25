package com.github.mangila.fibonacci.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.ErrorResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        var cors = new CorsConfiguration()
                .applyPermitDefaultValues();
        cors.addAllowedMethod(HttpMethod.OPTIONS);
        cors.addAllowedMethod(HttpMethod.DELETE);
        registry.addMapping("/**")
                .combine(cors);
    }

    @Override
    public void addErrorResponseInterceptors(List<ErrorResponse.Interceptor> interceptors) {
        interceptors.add((detail, errorResponse) -> {
            log.info("Detail: {}", detail);
            log.info("ErrorResponse: {}", errorResponse);
        });
    }
}
