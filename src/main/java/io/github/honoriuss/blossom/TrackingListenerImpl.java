package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingHandler;
import io.github.honoriuss.blossom.interfaces.ITrackingWriter;
import org.springframework.context.event.EventListener;

class TrackingListenerImpl implements ITrackingHandler<String> {
    private final ITrackingWriter<String> trackingWriter;

    TrackingListenerImpl(ITrackingWriter<String> trackingWriter) {
        this.trackingWriter = trackingWriter;
    }

    @Override
    @EventListener
    public void handleTracking(String message) {
        trackingWriter.write(message);
    }
}
