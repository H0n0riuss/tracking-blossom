package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingHandler;
import io.github.honoriuss.blossom.interfaces.ITrackingObjectMapper;
import io.github.honoriuss.blossom.interfaces.ITrackingWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
class TrackingConfig {

    @Bean
    @ConditionalOnMissingBean(ITrackingHandler.class)
    @ConditionalOnProperty(name = "blossom.event", havingValue = "false")
    public ITrackingHandler<String> getHandler(ITrackingWriter<String> trackingWriter) {
        return TrackingFactory.getDefaultTracking(trackingWriter);
    }

    @Bean
    @ConditionalOnMissingBean(ITrackingWriter.class)
    @ConditionalOnProperty(name = "blossom.event", havingValue = "false")
    public ITrackingWriter<String> getWriter() {
        return TrackingFactory.getDefaultWriter();
    }

    @Bean
    @ConditionalOnMissingBean(ITrackingObjectMapper.class)
    public ITrackingObjectMapper<String> getObjectMapper() {
        return TrackingFactory.getDefaultObjectMapper();
    }

    @Bean
    @ConditionalOnMissingBean(ITrackingHandler.class)
    @ConditionalOnProperty(name = "blossom.event", havingValue = "true")
    public ITrackingHandler<String> getListener(ApplicationEventPublisher applicationEventPublisher) {
        return TrackingFactory.getDefaultTrackingPublisher(applicationEventPublisher);
    }

    @Bean
    @ConditionalOnMissingBean(ITrackingWriter.class)
    @ConditionalOnProperty(name = "blossom.event", havingValue = "true")
    public ITrackingWriter<String> getListener() {
        return TrackingFactory.getDefaultListener();
    }
}
