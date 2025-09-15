package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BlossomWriterImpl<T> implements ITrackingWriter<T> {
    private final Logger logger = LoggerFactory.getLogger(BlossomWriterImpl.class);

    @Override
    public void write(T message) {
        logger.info("write: {}", message);
    }
}
