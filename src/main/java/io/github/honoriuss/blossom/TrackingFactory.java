package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.*;

import java.util.List;

abstract class TrackingFactory {
    public static ITrackingHandler<String> getDefaultTracking(ITrackingWriter<String> trackingWriter) {
        return new TrackingHandlerImpl(trackingWriter);
    }

    public static ITrackingObjectMapper<String> getDefaultObjectMapper(ITrackingParameterRegistry parameterRegistry) {
        return new TrackingObjectMapperImpl(parameterRegistry);
    }

    public static ITrackingWriter<String> getDefaultWriter() {
        return new TrackingWriterImpl();
    }

    public static ITrackingParameterRegistry getDefaultParameterRegistry(List<ITrackingParameterProvider> parameterProviderList) {
        return new TrackingParameterRegistryImpl(parameterProviderList);
    }
}
