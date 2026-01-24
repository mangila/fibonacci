package com.github.mangila.fibonacci.web;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import(PostgresTestContainerConfiguration.class)
public @interface PostgresTestContainer {
}
