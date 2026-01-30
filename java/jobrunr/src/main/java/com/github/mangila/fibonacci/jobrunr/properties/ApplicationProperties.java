package com.github.mangila.fibonacci.jobrunr.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

    private final JobConfig produce = new JobConfig();
    private final JobConfig consume = new JobConfig();
    private final Zset zset = new Zset();

    public JobConfig getProduce() {
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
