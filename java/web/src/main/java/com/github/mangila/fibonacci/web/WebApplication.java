package com.github.mangila.fibonacci.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;

import java.util.TreeMap;

@SpringBootApplication
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigurableEnvironment env = (ConfigurableEnvironment) event.getApplicationContext().getEnvironment();

        System.out.println("========= ALL RESOLVED PROPERTIES =========");

        TreeMap<String, Object> props = new TreeMap<>();
        env.getPropertySources().forEach(source -> {
            if (source instanceof EnumerablePropertySource) {
                for (String name : ((EnumerablePropertySource<?>) source).getPropertyNames()) {
                    props.put(name, env.getProperty(name));
                }
            }
        });

        props.forEach((key, value) -> System.out.printf("%s = %s%n", key, value));
        System.out.println("===========================================");
    }
}
