package io.github.honoriuss.blossom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "blossom.config")
class BlossomProperties {
    private boolean enableSessionTracking = true;

    public boolean isEnableSessionTracking() {
        return enableSessionTracking;
    }

    public void setEnableSessionTracking(boolean enableSessionTracking) {
        this.enableSessionTracking = enableSessionTracking;
    }
}
