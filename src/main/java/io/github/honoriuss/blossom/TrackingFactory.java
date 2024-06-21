package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingHandler;
import io.github.honoriuss.blossom.interfaces.ITrackingObjectMapper;

abstract class TrackingFactory {
    public static ITrackingHandler<String> getDefaultTracking() {
        return new TrackingHandlerImpl<>();
    }

    public static ITrackingObjectMapper<String> getDefaultObjectMapper() {
        return new TrackingObjectMapperImpl();
    }
}
