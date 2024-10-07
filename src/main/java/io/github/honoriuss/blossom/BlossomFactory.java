package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

abstract class BlossomFactory {
    public static ITrackingHandler<String> getDefaultTracking(ITrackingWriter<String> trackingWriter) {
        return new BlossomHandlerImpl(trackingWriter);
    }

    public static ITrackingObjectMapper<String> getDefaultObjectMapper(ITrackingParameterRegistry parameterRegistry) {
        return new BlossomObjectMapperImpl(parameterRegistry);
    }

    public static ITrackingWriter<String> getDefaultWriter() {
        return new BlossomWriterImpl();
    }

    public static ITrackingParameterRegistry getDefaultParameterRegistry(List<ITrackingParameterProvider> parameterProviderList) {
        return new BlossomParameterRegistryImpl(parameterProviderList);
    }

    public static ITrackingFilter getDefaultFilter(String sessionIdHeaderName, String timestampName) {
        return new BlossomFilterImpl(sessionIdHeaderName, timestampName);
    }

    public static ITrackingParameterProvider getOptionalHeaderParameterProvider(Map<String, String> headers, HttpServletRequest request) {
        return new BlossomOptionalParameterProviderImpl(headers, request);
    }

    public static ITrackingAppContextHandler getDefaultAppContextHandler() {
        return new BlossomAppContextHandlerImpl();
    }
}
