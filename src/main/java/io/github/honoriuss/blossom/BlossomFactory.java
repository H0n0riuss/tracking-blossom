package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.*;

import java.util.List;

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

    public static ITrackingFilter getDefaultFilter() {
        return new BlossomFilterImpl();
    }
}
