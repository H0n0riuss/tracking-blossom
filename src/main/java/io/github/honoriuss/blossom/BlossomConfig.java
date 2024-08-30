package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.List;

@Configuration
@EnableAspectJAutoProxy
class BlossomConfig {

    @Bean
    @ConditionalOnMissingBean(ITrackingHandler.class)
    public ITrackingHandler<String> getHandler(ITrackingWriter<String> trackingWriter) {
        return BlossomFactory.getDefaultTracking(trackingWriter);
    }

    @Bean
    @ConditionalOnMissingBean(ITrackingWriter.class)
    public ITrackingWriter<String> getWriter() {
        return BlossomFactory.getDefaultWriter();
    }

    @Bean
    @ConditionalOnMissingBean(ITrackingObjectMapper.class)
    public ITrackingObjectMapper<String> getObjectMapper(ITrackingParameterRegistry parameterRegistry) {
        return BlossomFactory.getDefaultObjectMapper(parameterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean(ITrackingParameterRegistry.class)
    public ITrackingParameterRegistry getDefaultParameterRegistry(List<ITrackingParameterProvider> parameterProviderList) {
        return BlossomFactory.getDefaultParameterRegistry(parameterProviderList);
    }
}
