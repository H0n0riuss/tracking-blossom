package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingHandler;
import io.github.honoriuss.blossom.interfaces.ITrackingObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;

@Configuration
@EnableAspectJAutoProxy
class TrackingConfig {

    @Bean
    @Order
    @ConditionalOnMissingBean(ITrackingHandler.class)
    public ITrackingHandler<String> getHandler() {
        return TrackingFactory.getDefaultTracking();
    }

    @Bean
    @Order
    @ConditionalOnMissingBean(ITrackingObjectMapper.class)
    public ITrackingObjectMapper<String> getObjectMapper(TrackingProperties trackingProperties) {
        return TrackingFactory.getDefaultObjectMapper(trackingProperties);
    }
}
