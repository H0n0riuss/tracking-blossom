package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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

    @Bean
    @ConditionalOnProperty(name = "blossom.config.enabled", havingValue = "true")
    public ITrackingFilter getFilter(FilterRegistrationBean<ITrackingFilter> filterRegistrationBean, BlossomPropertiesConfig blossomPropertiesConfig) {
        var blossom = BlossomFactory.getDefaultFilter(blossomPropertiesConfig.getSessionIdName(), blossomPropertiesConfig.getTimestampName());
        filterRegistrationBean.setFilter(blossom);
        return blossom;
    }

    @Bean
    @ConditionalOnProperty(name = "blossom.config.enabled", havingValue = "true")
    public FilterRegistrationBean<ITrackingFilter> createFilterRegistrationBean() {
        return new FilterRegistrationBean<>();
    }

    @Bean
    public ITrackingParameterProvider getOptionalHeaderParameterProvider(BlossomPropertiesOptional blossomPropertiesOptional, HttpServletRequest request) {
        if (blossomPropertiesOptional.isMapNotEmpty()) {
            return BlossomFactory.getOptionalHeaderParameterProvider(blossomPropertiesOptional.getHeaders(), request);
        }
        return null;
    }
}
