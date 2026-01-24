package com.github.mangila.fibonacci.scheduler;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@TestPropertySource(properties = {
        "spring.flyway.locations=filesystem:../../java/flyway/{vendor}"
})
@Import(PostgresTestContainerConfiguration.class)
public @interface PostgresTestContainer {
}
