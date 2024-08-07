package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingHandler;
import io.github.honoriuss.blossom.interfaces.ITrackingObjectMapper;
import io.github.honoriuss.blossom.interfaces.ITrackingWriter;
import org.springframework.context.ApplicationEventPublisher;

abstract class TrackingFactory {
    public static ITrackingHandler<String> getDefaultTracking(ITrackingWriter<String> trackingWriter) {
        return new TrackingHandlerImpl(trackingWriter);
    }

    public static ITrackingObjectMapper<String> getDefaultObjectMapper() {
        return new TrackingObjectMapperImpl();
    }

    public static ITrackingHandler<String> getDefaultTrackingPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new TrackingPublisherImpl(applicationEventPublisher);
    }

    public static ITrackingWriter<String> getDefaultWriter() {
        return new TrackingWriterImpl();
    }

    public static ITrackingWriter<String> getDefaultListener() {
        return new TrackingListenerImpl();
    }
}
