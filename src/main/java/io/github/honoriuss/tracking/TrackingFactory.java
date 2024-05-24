package io.github.honoriuss.tracking;

import io.github.honoriuss.tracking.interfaces.ITrackingHandler;
import io.github.honoriuss.tracking.interfaces.ITrackingObjectMapper;

abstract class TrackingFactory {
    public static ITrackingHandler<String> getDefaultTracking() {
        return new TrackingHandlerImpl<>();
    }

    public static ITrackingObjectMapper<String> getDefaultObjectMapper(TrackingProperties trackingProperties) {
        return new TrackingObjectMapperImpl(trackingProperties);
    }
}
