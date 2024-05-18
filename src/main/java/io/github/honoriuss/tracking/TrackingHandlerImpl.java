package io.github.honoriuss.tracking;

import io.github.honoriuss.tracking.interfaces.ITrackingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TrackingHandlerImpl implements ITrackingHandler {
    private final Logger logger = LoggerFactory.getLogger(TrackingHandlerImpl.class);

    @Override
    public void handleTracking(String message) {
        logger.info("Handling tracking event: {}", message);
    }
}
