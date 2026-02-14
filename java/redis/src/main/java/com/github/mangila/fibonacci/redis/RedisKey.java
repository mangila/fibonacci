package com.github.mangila.fibonacci.redis;

import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.ops.EnsureStringOps;

public record RedisKey(String value) {

    private static final EnsureStringOps ENSURE_STRING_OPS = Ensure.strings();

    public RedisKey {
        ENSURE_STRING_OPS.notBlank(value, "Value must not be blank");
    }
}
