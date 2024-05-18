package io.github.honoriuss.tracking;

import io.github.honoriuss.tracking.interfaces.ITrackingHandler;

abstract class TrackingFactory {
    public static ITrackingHandler getDefaultTracking() {
        return new TrackingHandlerImpl();
    }
}
