package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.shared.FibonacciAlgorithm;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.job.producer")
@Validated
public class ProducerProperties {

    private boolean enabled = false;
    @Positive
    private int limit = 50;
    @Positive
    private int batchSize = 100;
    private FibonacciAlgorithm algorithm = FibonacciAlgorithm.ITERATIVE;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public FibonacciAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(FibonacciAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}
