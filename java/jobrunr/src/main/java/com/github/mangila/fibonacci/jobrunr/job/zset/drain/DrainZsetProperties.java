package com.github.mangila.fibonacci.jobrunr.job.zset.drain;

import org.intellij.lang.annotations.Language;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.job.zset.drain")
public class DrainZsetProperties {

    private boolean enabled = false;
    private int limit = 50;
    @Language("CronExp")
    private String cron = "0 0/1 * * * *";

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

    public String getCron() {
        return cron;
    }

    public void setCron(@Language("CronExp") String cron) {
        this.cron = cron;
    }
}
