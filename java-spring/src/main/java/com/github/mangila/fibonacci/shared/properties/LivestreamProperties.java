package com.github.mangila.fibonacci.shared.properties;

import com.github.mangila.fibonacci.shared.annotation.ValidChannel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.livestream")
@Validated
public class LivestreamProperties {

    @ValidChannel
    private String pgChannel = "livestream";
    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPgChannel() {
        return pgChannel;
    }

    public void setPgChannel(String pgChannel) {
        this.pgChannel = pgChannel;
    }
}
