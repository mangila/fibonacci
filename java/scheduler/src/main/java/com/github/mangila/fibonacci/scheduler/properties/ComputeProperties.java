package com.github.mangila.fibonacci.scheduler.properties;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.compute")
@Validated
public class ComputeProperties {

    @Positive
    private int max = 10_00;

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
