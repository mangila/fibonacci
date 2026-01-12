package com.github.mangila.fibonacci.config;

import com.github.mangila.fibonacci.FibonacciAlgorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "app.fibonacci")
public class FibonacciProperties {

    private FibonacciAlgorithm algorithm = FibonacciAlgorithm.ITERATIVE;
    private int limit = 1000;
    private Duration delay = Duration.ofSeconds(1);

    public void setAlgorithm(FibonacciAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public FibonacciAlgorithm getAlgorithm() {
        return algorithm;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Duration getDelay() {
        return delay;
    }

    public void setDelay(Duration delay) {
        this.delay = delay;
    }
}
