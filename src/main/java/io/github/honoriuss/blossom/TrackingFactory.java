package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingHandler;
import io.github.honoriuss.blossom.interfaces.ITrackingObjectMapper;
import io.github.honoriuss.blossom.interfaces.ITrackingWriter;

abstract class TrackingFactory {
    public static ITrackingHandler<String> getDefaultTracking(ITrackingWriter<String> trackingWriter) {
        return new TrackingHandlerImpl(trackingWriter);
    }

    public static ITrackingObjectMapper<String> getDefaultObjectMapper() {
        return new TrackingObjectMapperImpl();
    }

    public static ITrackingHandler<String> getDefaultTrackingListener() {
        return new TrackingListenerImpl();
    }

    public static ITrackingWriter<String> getDefaultWriter() {
        return new TrackingWriterImpl();
    }
}
