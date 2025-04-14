package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "io.github.honoriuss.blossom")
class BlossomConfig<T> {
    private final Logger logger = LoggerFactory.getLogger(BlossomConfig.class.getName());

    @Bean
    @ConditionalOnMissingBean(ITrackingHandler.class)
    public ITrackingHandler<T> getHandler(ITrackingWriter<T> trackingWriter) {
        logger.debug("Using DefaultHandler");
        logger.debug("Using Writer: {}", trackingWriter.getClass().getSimpleName());
        return BlossomFactory.getDefaultTrackingHandler(trackingWriter);
    }

    @Bean
    @ConditionalOnMissingBean(ITrackingWriter.class)
    public ITrackingWriter<T> getWriter() {
        logger.debug("Using DefaultTrackingWriter");
        return BlossomFactory.getDefaultWriter();
    }

    @Bean
    @ConditionalOnMissingBean(ITrackingObjectMapper.class)
    public ITrackingObjectMapper<String> getObjectMapper(ITrackingParameterRegistry parameterRegistry) {
        logger.debug("Using DefaultTrackingObjectMapper");
        return BlossomFactory.getDefaultObjectMapper(parameterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean(ITrackingParameterRegistry.class)
    public ITrackingParameterRegistry getDefaultParameterRegistry(List<ITrackingParameterProvider> parameterProviderList) {
        logger.debug("Using DefaultTrackingParameterRegistry");
        return BlossomFactory.getDefaultParameterRegistry(parameterProviderList);
    }

    @Bean
    @ConditionalOnMissingBean(ITrackingAppContextHandler.class)
    public ITrackingAppContextHandler getDefaultAppContextHandler() {
        logger.debug("Using DefaultTrackingAppContextHandler");
        return BlossomFactory.getDefaultAppContextHandler();
    }

    @Bean
    @ConditionalOnProperty(name = "blossom.config.enabled", havingValue = "true")
    public ITrackingFilter getFilter(FilterRegistrationBean<ITrackingFilter> filterRegistrationBean, BlossomPropertiesConfig blossomPropertiesConfig) {
        logger.debug("Using TrackingDefaultFilter: FilterRegistrationBean: {}", filterRegistrationBean.getClass().getSimpleName());
        var blossom = BlossomFactory.getDefaultFilter(blossomPropertiesConfig.getSessionIdName(), blossomPropertiesConfig.getTimestampName());
        filterRegistrationBean.setFilter(blossom);
        return blossom;
    }

    @Bean
    @ConditionalOnProperty(name = "blossom.config.enabled", havingValue = "true")
    public FilterRegistrationBean<ITrackingFilter> createFilterRegistrationBean() {
        logger.debug("Using DefaultTrackingRegistrationBean");
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
