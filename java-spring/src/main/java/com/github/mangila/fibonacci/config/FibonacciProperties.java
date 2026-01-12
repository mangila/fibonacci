package com.github.mangila.fibonacci.config;

import com.github.mangila.fibonacci.FibonacciAlgorithm;
import io.github.mangila.ensure4j.Ensure;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "app.fibonacci")
public class FibonacciProperties {

    private FibonacciAlgorithm algorithm = FibonacciAlgorithm.ITERATIVE;
    private int offset = 1;
    private int limit = 1000;
    private Duration delay = Duration.ofSeconds(1);

    public FibonacciAlgorithm getAlgorithm() {
        return algorithm;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }


    public Duration getDelay() {
        return delay;
    }

    /**
     * The algorithm to use for computing Fibonacci numbers.
     */
    public void setAlgorithm(FibonacciAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * The maximum Fibonacci index to compute.
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * The delay between computation tasks.
     */
    public void setDelay(Duration delay) {
        this.delay = delay;
    }

    /**
     * The offset to start computing Fibonacci numbers from.
     */
    public void setOffset(int offset) {
        Ensure.min(1, offset);
        this.offset = offset;
    }
}
