package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BlossomWriterImpl implements ITrackingWriter<String> {
    private final Logger logger = LoggerFactory.getLogger(BlossomWriterImpl.class);

    @Override
    public void write(String message) {
        logger.info("write: {}", message);
    }
}
