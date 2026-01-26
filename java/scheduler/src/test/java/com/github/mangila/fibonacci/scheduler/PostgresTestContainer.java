package com.github.mangila.fibonacci.scheduler;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@TestPropertySource(properties = {
        "jobrunr.dashboard.enabled=false",
        "springdoc.swagger-ui.enabled=false"
})
@Import(PostgresTestContainerConfiguration.class)
public @interface PostgresTestContainer {
}
