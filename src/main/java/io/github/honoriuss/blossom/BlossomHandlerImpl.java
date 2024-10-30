package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingHandler;
import io.github.honoriuss.blossom.interfaces.ITrackingWriter;

class BlossomHandlerImpl<T> implements ITrackingHandler<T> {
    private final ITrackingWriter<T> trackingWriter;

    BlossomHandlerImpl(ITrackingWriter<T> trackingWriter) {
        this.trackingWriter = trackingWriter;
    }

    @Override
    public void handleTracking(T message) {
        trackingWriter.write(message);
    }
}
