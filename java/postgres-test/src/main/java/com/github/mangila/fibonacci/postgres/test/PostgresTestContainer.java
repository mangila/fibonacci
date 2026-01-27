package com.github.mangila.fibonacci.postgres.test;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import(PostgresTestContainerConfiguration.class)
public @interface PostgresTestContainer {
}
