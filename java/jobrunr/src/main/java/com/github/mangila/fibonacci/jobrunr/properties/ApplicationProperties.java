package com.github.mangila.fibonacci.jobrunr.properties;

import com.github.mangila.fibonacci.core.FibonacciAlgorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

    private final ProducerConfig produce = new ProducerConfig();
    private final JobConfig consume = new JobConfig();
    private final Zset zset = new Zset();

    public ProducerConfig getProduce() {
        return produce;
    }

    public JobConfig getConsume() {
        return consume;
    }

    public Zset getZset() {
        return zset;
    }

    public static class JobConfig {
        private boolean enabled = false;
        private int limit = 50;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }
    }

    public static class ProducerConfig extends JobConfig {

        private FibonacciAlgorithm algorithm = FibonacciAlgorithm.ITERATIVE;

        public FibonacciAlgorithm getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(FibonacciAlgorithm algorithm) {
            this.algorithm = algorithm;
        }

    }

    public static class Zset {
        private final JobConfig drain = new JobConfig();
        private final JobConfig insert = new JobConfig();

        public JobConfig getDrain() {
            return drain;
        }

        public JobConfig getInsert() {
            return insert;
        }
    }
}
