package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TrackingHandlerImpl implements ITrackingHandler<String> {
    private final Logger logger = LoggerFactory.getLogger(TrackingHandlerImpl.class);

    @Override
    public void handleTracking(String message) {
        logger.info("Handle tracking event: {}", message);
    }
}
