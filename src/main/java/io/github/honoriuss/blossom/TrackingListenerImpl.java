package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;

class TrackingListenerImpl implements ITrackingWriter<String> {
    private final Logger logger = LoggerFactory.getLogger(TrackingListenerImpl.class);

    @Override
    @EventListener
    public void write(String message) {
        logger.info("Write event: {}", message);
    }
}
