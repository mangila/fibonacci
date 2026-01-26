package com.github.mangila.fibonacci.web;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import(PostgresTestContainerConfiguration.class)
@TestPropertySource(properties = {
        "springdoc.swagger-ui.enabled=false"
})
public @interface PostgresTestContainer {
}
