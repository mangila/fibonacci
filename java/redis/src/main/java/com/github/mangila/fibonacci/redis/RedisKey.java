package com.github.mangila.fibonacci.redis;

import io.github.mangila.ensure4j.Ensure;

public record RedisKey(String value) {

    public RedisKey {
        Ensure.notBlank(value, "Value must not be blank");
    }
}
