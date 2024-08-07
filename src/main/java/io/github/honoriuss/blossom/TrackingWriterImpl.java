package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TrackingWriterImpl implements ITrackingWriter<String> {
    private final Logger logger = LoggerFactory.getLogger(TrackingWriterImpl.class);

    @Override
    public void write(String message) {
        logger.info("write: {}", message);
    }
}
