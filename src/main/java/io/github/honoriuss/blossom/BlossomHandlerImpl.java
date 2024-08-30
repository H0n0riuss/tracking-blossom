package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingHandler;
import io.github.honoriuss.blossom.interfaces.ITrackingWriter;

class BlossomHandlerImpl implements ITrackingHandler<String> {
    private final ITrackingWriter<String> trackingWriter;

    BlossomHandlerImpl(ITrackingWriter<String> trackingWriter) {
        this.trackingWriter = trackingWriter;
    }

    @Override
    public void handleTracking(String message) {
        trackingWriter.write(message);
    }
}
