package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import org.intellij.lang.annotations.Language;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.job.producer")
public class ProducerProperties {

    private boolean enabled = false;
    private int limit = 50;
    private FibonacciAlgorithm algorithm = FibonacciAlgorithm.ITERATIVE;
    @Language("CronExp")
    private String cron = "0 0/1 * * * *";

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
    }

    public FibonacciAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(FibonacciAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(@Language("CronExp") String cron) {
        this.cron = cron;
    }
}
