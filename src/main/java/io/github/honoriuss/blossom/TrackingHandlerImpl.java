package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingHandler;
import io.github.honoriuss.blossom.interfaces.ITrackingWriter;

class TrackingHandlerImpl implements ITrackingHandler<String> {
    private final ITrackingWriter<String> trackingWriter;

    TrackingHandlerImpl(ITrackingWriter<String> trackingWriter) {
        this.trackingWriter = trackingWriter;
    }

    @Override
    public void handleTracking(String message) {
        trackingWriter.write(message);
    }
}
