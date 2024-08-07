package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingHandler;
import io.github.honoriuss.blossom.interfaces.ITrackingObjectMapper;
import io.github.honoriuss.blossom.interfaces.ITrackingWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
class TrackingConfig {

    @Bean
    @ConditionalOnProperty(name = "blossom.listen", havingValue = "false")
    @ConditionalOnMissingBean(ITrackingHandler.class)
    public ITrackingHandler<String> getHandler(ITrackingWriter<String> trackingWriter) {
        return TrackingFactory.getDefaultTracking(trackingWriter);
    }

    @Bean
    @ConditionalOnMissingBean(ITrackingHandler.class)
    @ConditionalOnProperty(name = "blossom.listen", havingValue = "true")
    public ITrackingHandler<String> getListener(ITrackingWriter<String> trackingWriter) {
        return TrackingFactory.getDefaultTrackingListener(trackingWriter);
    }

    @Bean
    @ConditionalOnMissingBean(ITrackingObjectMapper.class)
    public ITrackingObjectMapper<String> getObjectMapper() {
        return TrackingFactory.getDefaultObjectMapper();
    }

    @Bean
    @ConditionalOnMissingBean(ITrackingWriter.class)
    public ITrackingWriter<String> getWriter() {
        return TrackingFactory.getDefaultWriter();
    }
}
