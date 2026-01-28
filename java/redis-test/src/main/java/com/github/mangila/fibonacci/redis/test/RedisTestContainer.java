package com.github.mangila.fibonacci.redis.test;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import(RedisTestContainerConfiguration.class)
public @interface RedisTestContainer {
}
