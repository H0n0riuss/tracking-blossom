package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TrackingHandlerImpl<T> implements ITrackingHandler<T> {
    private final Logger logger = LoggerFactory.getLogger(TrackingHandlerImpl.class);

    @Override
    public void handleTracking(T message) {
        logger.info("Handling tracking event: {}", message);
    }
}
