package com.github.mangila.fibonacci.config;

import com.github.mangila.fibonacci.FibonacciAlgorithm;
import io.github.mangila.ensure4j.Ensure;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.fibonacci")
@Validated
public class FibonacciProperties {

    private FibonacciAlgorithm algorithm = FibonacciAlgorithm.ITERATIVE;
    @Min(0)
    private int offset = 0;
    @Min(1)
    private int limit = 1000;
    private Duration delay = Duration.ofSeconds(1);

    public FibonacciAlgorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * Sets the algorithm to be used for calculating Fibonacci sequences.
     *
     * @param algorithm the Fibonacci algorithm to be applied, such as ITERATIVE, RECURSIVE, or FAST_DOUBLING
     */
    public void setAlgorithm(FibonacciAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public int getOffset() {
        return offset;
    }

    /**
     * Sets the offset value for the Fibonacci sequence calculation.
     * The offset determines the starting point of the sequence.
     *
     * @param offset the offset value, must be a non-negative integer
     */
    public void setOffset(int offset) {
        Ensure.min(0, offset);
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    /**
     * Sets the limit value for the Fibonacci sequence calculation.
     * The limit defines the maximum number of terms to generate in the sequence.
     *
     * @param limit the maximum number of terms, must be a positive integer
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Duration getDelay() {
        return delay;
    }

    /**
     * Sets the delay duration for processing Fibonacci calculations.
     * This delay can be used to simulate latency or throttle requests.
     *
     * @param delay the duration to be set as the delay; must not be null
     */
    public void setDelay(Duration delay) {
        this.delay = delay;
    }
}
