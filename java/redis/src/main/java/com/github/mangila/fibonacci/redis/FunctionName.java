package com.github.mangila.fibonacci.redis;

import io.github.mangila.ensure4j.Ensure;

public record FunctionName(String value) {

    public FunctionName {
        Ensure.notBlank(value, "Value must not be blank");
    }

}
