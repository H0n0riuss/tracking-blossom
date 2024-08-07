package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingHandler;
import org.springframework.context.ApplicationEventPublisher;

class TrackingPublisherImpl implements ITrackingHandler<String> {
    private final ApplicationEventPublisher applicationEventPublisher;

    TrackingPublisherImpl(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void handleTracking(String message) {
        applicationEventPublisher.publishEvent(message);
    }
}
